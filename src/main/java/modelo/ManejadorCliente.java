package modelo;

import modelo.Mensaje;
import org.hibernate.Session;
import org.hibernate.Transaction;

import controlador.Server;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private String nombreUsuario;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    public void enviarMensaje(String msg) {
        if (out != null)
            out.println(msg);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);

            // Registro inicial: El primer mensaje del cliente es su nombre
            this.nombreUsuario = in.readLine();
            Server.mapaClientes.put(nombreUsuario, this);
            // broadcast("SISTEMA: " + nombreUsuario + " se ha unido al chat."); FRONTEND
            // LUZ VERDE CONECTADO

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("@")) {
                    enviarPrivado(msg);
                } else if (msg.startsWith("/congelar ")) {
                    procesarMensajeCongelado(msg);
                } else {
                    // Guardar en MySQL
                    guardarEnBD(nombreUsuario, msg);
                    // Retransmitir a todos
                    broadcast(nombreUsuario + ": " + msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Conexión perdida con " + nombreUsuario);
        } finally {
            if (nombreUsuario != null) {
                Server.mapaClientes.remove(nombreUsuario);
                // broadcast("SISTEMA: " + nombreUsuario + " ha salido."); FRONTEND LUZ ROJA
                // DESCONECTADO
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private void guardarEnBD(String emisor, String texto) {
        // Usamos la fábrica estática del Server
        try (Session session = Server.sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(new Mensaje(emisor, texto));
            t.commit();
            System.out.println("[DB] Mensaje de " + emisor + " guardado.");
        } catch (Exception e) {
            System.err.println("[Error DB] No se pudo guardar: " + e.getMessage());
        }
    }

    private void enviarPrivado(String msgCompleto) {
        int primerEspacio = msgCompleto.indexOf(" ");
        if (primerEspacio != -1) {
            String destino = msgCompleto.substring(1, primerEspacio);
            String contenido = msgCompleto.substring(primerEspacio + 1);

            ManejadorCliente receptor = Server.mapaClientes.get(destino);
            if (receptor != null) {
                receptor.enviarMensaje("(Privado de " + nombreUsuario + "): " + contenido);
                this.enviarMensaje("(Privado para " + destino + "): " + contenido);
            } else {
                this.enviarMensaje("SISTEMA: Usuario " + destino + " no encontrado.");
            }
        }
    }

    private void broadcast(String mensaje) {
        for (ManejadorCliente c : Server.mapaClientes.values()) {
            c.enviarMensaje(mensaje);
        }
    }

    private void procesarMensajeCongelado(String msg) {
        // Formato: /congelar 1h @Usuario Mensaje secreto
        // Formato: /congelar 10m Mensaje publico
        try {
            String[] partes = msg.split(" ", 3);
            if (partes.length < 3) {
                enviarMensaje("SISTEMA: Uso incorrecto. Ejemplo: /congelar 10m Hola (o /freeze 10m Hola)");
                return;
            }

            String tiempoStr = partes[1];
            String restoDelMensaje = partes[2];
            long cantidad = Long.parseLong(tiempoStr.replaceAll("[^0-9]", ""));
            LocalDateTime fechaVisible = LocalDateTime.now();

            if (tiempoStr.endsWith("d")) {
                fechaVisible = fechaVisible.plusDays(cantidad);
            } else if (tiempoStr.endsWith("h")) {
                fechaVisible = fechaVisible.plusHours(cantidad);
            } else if (tiempoStr.endsWith("s")) {
                fechaVisible = fechaVisible.plusSeconds(cantidad);
            } else {
                fechaVisible = fechaVisible.plusMinutes(cantidad);
            }

            String destinatario = null;
            String contenidoReal = restoDelMensaje;

            // Detectar si es privado
            if (restoDelMensaje.startsWith("@")) {
                int espacio = restoDelMensaje.indexOf(" ");
                if (espacio != -1) {
                    destinatario = restoDelMensaje.substring(1, espacio);
                    contenidoReal = restoDelMensaje.substring(espacio + 1);
                }
            }

            try (Session session = Server.sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                // Constructor: emisor, contenido, fechaVisible, destinatario
                session.persist(new Mensaje(nombreUsuario, contenidoReal, fechaVisible, destinatario));
                t.commit();

                enviarMensaje("SISTEMA: ❄️ Mensaje congelado creado.");

                // Notificar "Countdown"
                java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
                String notifMsg = "⏳ " + nombreUsuario + " ha enviado un mensaje congelado. Se revelará en " + tiempoStr
                        + " (a las " + fechaVisible.format(timeFmt) + ").";

                if (destinatario != null) {
                    // Es privado
                    ManejadorCliente receptor = Server.mapaClientes.get(destinatario);
                    if (receptor != null) {
                        receptor.enviarMensaje(notifMsg + " (Solo para ti)");
                    } else {
                        enviarMensaje("SISTEMA: El destinatario " + destinatario
                                + " no está conectado, pero verá el mensaje cuando se descongele (si se conecta).");
                    }
                } else {
                    // Es público -> Broadcast
                    broadcast(notifMsg);
                }

            } catch (Exception e) {
                enviarMensaje("SISTEMA: Error al guardar mensaje congelado.");
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            enviarMensaje("SISTEMA: Formato de tiempo inválido.");
        }
    }
}
