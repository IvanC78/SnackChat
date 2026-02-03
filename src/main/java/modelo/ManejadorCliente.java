package modelo;

import modelo.Mensaje;
import org.hibernate.Session;
import org.hibernate.Transaction;

import controlador.Server;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private Usuario usuario;
    private Chat chat = ChatDAO.obtenerPorId((long)1);

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    public void enviarMensaje(String msg) {
        if (out != null) out.println(msg);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);

            // Registro inicial: El primer mensaje del cliente es su nombre
            this.usuario = UsuarioDAO.buscarPorNombre(in.readLine());
            Server.mapaClientes.put(usuario, this);

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("@")) {
                    enviarPrivado(msg);
                } else if (msg.startsWith("/congelar ")) {
                    procesarCongelar(msg);
                } else if (msg.startsWith("/quemar ")) {
                    procesarQuemar(msg);
                } else {
                    // Guardar en MySQL
                    guardarEnBD(usuario, chat,  msg);
                    // Retransmitir a todos
                    Server.broadcast(usuario.getNombreUsuario() + ": " + msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Conexión perdida con " + usuario.getNombreUsuario());
        } finally {
            if (usuario != null) {
                Server.mapaClientes.remove(usuario);
            }
            try { socket.close(); } catch (IOException e) {}
        }
    }
    
    /**
     * Procesa el comando /congelar tiempo mensaje
     * Ejemplo: /congelar 10s Hola mundo
     */
    private void procesarCongelar(String comando) {
        try {
            // Extraer tiempo y mensaje
            Pattern pattern = Pattern.compile("^/congelar\\s+(\\d+)([smhd])\\s+(.+)$");
            Matcher matcher = pattern.matcher(comando);
            
            if (!matcher.matches()) {
                enviarMensaje("❌ Formato incorrecto. Uso: /congelar 10s Tu mensaje aquí");
                return;
            }
            
            int cantidad = Integer.parseInt(matcher.group(1));
            String unidad = matcher.group(2);
            String mensaje = matcher.group(3);
            
            LocalDateTime fechaAccion = calcularFechaAccion(cantidad, unidad);
            
            if (fechaAccion == null) {
                enviarMensaje("❌ Tiempo inválido. Rango: 1s a 30d");
                return;
            }
            
            // Guardar mensaje congelado en BD
            guardarMensajeCongelado(usuario, chat, mensaje, fechaAccion);
            
            // Notificar al usuario
            String tiempoTexto = cantidad + unidad;
            enviarMensaje("❄️ Mensaje congelado. Se descongelará en " + tiempoTexto);
            Server.broadcast("❄️ SISTEMA: " + usuario + " ha enviado un mensaje congelado que se mostrará en " + tiempoTexto);
            
        } catch (Exception e) {
            enviarMensaje("❌ Error al procesar comando /congelar: " + e.getMessage());
        }
    }
    
    /**
     * Procesa el comando /quemar tiempo mensaje
     * Ejemplo: /quemar 5m Mensaje secreto
     */
    private void procesarQuemar(String comando) {
        try {
            // Extraer tiempo y mensaje
            Pattern pattern = Pattern.compile("^/quemar\\s+(\\d+)([smhd])\\s+(.+)$");
            Matcher matcher = pattern.matcher(comando);
            
            if (!matcher.matches()) {
                enviarMensaje("❌ Formato incorrecto. Uso: /quemar 5m Tu mensaje aquí");
                return;
            }
            
            int cantidad = Integer.parseInt(matcher.group(1));
            String unidad = matcher.group(2);
            String mensaje = matcher.group(3);
            
            LocalDateTime fechaAccion = calcularFechaAccion(cantidad, unidad);
            
            if (fechaAccion == null) {
                enviarMensaje("❌ Tiempo inválido. Rango: 1s a 30d");
                return;
            }
            
            // Guardar mensaje para quemar en BD
            guardarMensajeQuemar(usuario, chat, mensaje, fechaAccion);
            
            // Mostrar mensaje inmediatamente
            String tiempoTexto = cantidad + unidad;
            Server.broadcast("🔥 " + usuario.getNombreUsuario() + ": " + mensaje + " (se quemará en " + tiempoTexto + ")");
            
        } catch (Exception e) {
            enviarMensaje("❌ Error al procesar comando /quemar: " + e.getMessage());
        }
    }
    
    /**
     * Calcula la fecha de acción según el tiempo especificado
     * Valida que esté entre 1s y 30d
     */
    private LocalDateTime calcularFechaAccion(int cantidad, String unidad) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaAccion;
        
        switch (unidad) {
            case "s": // segundos
                if (cantidad < 1 || cantidad > 2592000) return null; // max 30 días en segundos
                fechaAccion = ahora.plusSeconds(cantidad);
                break;
            case "m": // minutos
                if (cantidad < 1 || cantidad > 43200) return null; // max 30 días en minutos
                fechaAccion = ahora.plusMinutes(cantidad);
                break;
            case "h": // horas
                if (cantidad < 1 || cantidad > 720) return null; // max 30 días en horas
                fechaAccion = ahora.plusHours(cantidad);
                break;
            case "d": // días
                if (cantidad < 1 || cantidad > 30) return null;
                fechaAccion = ahora.plusDays(cantidad);
                break;
            default:
                return null;
        }
        
        return fechaAccion;
    }

    private void guardarEnBD(Usuario emisor, Chat chat, String texto) {
        // Usamos la fábrica estática del Server
        try (Session session = Server.sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(new Mensaje(emisor, chat, texto));
            t.commit();
            System.out.println("[DB] Mensaje de " + emisor + " guardado.");
        } catch (Exception e) {
            System.err.println("[Error DB] No se pudo guardar: " + e.getMessage());
        }
    }
    
    private void guardarMensajeCongelado(Usuario usuario, Chat chat,  String texto, LocalDateTime fechaAccion) {
        try (Session session = Server.sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            Mensaje mensaje = new Mensaje(usuario, chat, texto, "CONGELADO", fechaAccion);
            session.persist(mensaje);
            t.commit();
            System.out.println("[DB] Mensaje congelado de " + usuario.getNombreUsuario() + " guardado.");
        } catch (Exception e) {
            System.err.println("[Error DB] No se pudo guardar mensaje congelado: " + e.getMessage());
        }
    }
    
    private void guardarMensajeQuemar(Usuario usuario, Chat chat, String texto, LocalDateTime fechaAccion) {
        try (Session session = Server.sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            Mensaje mensaje = new Mensaje(usuario, chat, texto, "QUEMAR", fechaAccion);
            session.persist(mensaje);
            t.commit();
            System.out.println("[DB] Mensaje para quemar de " + usuario.getNombreUsuario() + " guardado.");
        } catch (Exception e) {
            System.err.println("[Error DB] No se pudo guardar mensaje para quemar: " + e.getMessage());
        }
    }

    private void enviarPrivado(String msgCompleto) {
        int primerEspacio = msgCompleto.indexOf(" ");
        if (primerEspacio != -1) {
            String destino = msgCompleto.substring(1, primerEspacio);
            String contenido = msgCompleto.substring(primerEspacio + 1);

            ManejadorCliente receptor = Server.mapaClientes.get(destino);
            if (receptor != null) {
                receptor.enviarMensaje("(Privado de " + usuario.getNombreUsuario() + "): " + contenido);
                this.enviarMensaje("(Privado para " + destino + "): " + contenido);
            } else {
                this.enviarMensaje("SISTEMA: Usuario " + destino + " no encontrado.");
            }
        }
    }
}