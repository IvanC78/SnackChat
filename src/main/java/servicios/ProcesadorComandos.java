package servicios;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import controlador.Server;
import modelo.ManejadorCliente;
import modelo.Mensaje;

public class ProcesadorComandos {

    public static boolean procesar(String msg, ManejadorCliente cliente) {
        if (msg.startsWith("/congelar ") || msg.startsWith("/freeze ")) {
            procesarMensajeCongelado(msg, cliente);
            return true;
        } else if (msg.startsWith("/quemar ") || msg.startsWith("/burn ")) {
            procesarMensajeQuemado(msg, cliente);
            return true;
        }
        return false;
    }

    private static void procesarMensajeCongelado(String msg, ManejadorCliente cliente) {
        // Formato: /congelar 1h @Usuario Mensaje secreto
        try {
            String[] partes = msg.split(" ", 3);
            if (partes.length < 3) {
                cliente.enviarMensaje("SISTEMA: Uso incorrecto. Ejemplo: /congelar 10m Hola (o /freeze 10m Hola)");
                return;
            }

            String tiempoStr = partes[1];
            String restoDelMensaje = partes[2];
            long cantidad = parseTiempo(tiempoStr);
            LocalDateTime fechaVisible = calcularFechaFutura(tiempoStr, cantidad);

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
                session.persist(new Mensaje(getNombreCliente(cliente), contenidoReal, fechaVisible, destinatario));
                t.commit();

                cliente.enviarMensaje("SISTEMA: ❄️ Mensaje congelado creado.");

                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
                String notifMsg = "⏳ " + getNombreCliente(cliente) + " ha enviado un mensaje congelado. Se revelará en "
                        + tiempoStr
                        + " (a las " + fechaVisible.format(timeFmt) + ").";

                if (destinatario != null) {
                    ManejadorCliente receptor = Server.mapaClientes.get(destinatario);
                    if (receptor != null) {
                        receptor.enviarMensaje(notifMsg + " (Solo para ti)");
                    } else {
                        cliente.enviarMensaje("SISTEMA: El destinatario " + destinatario
                                + " no está conectado, pero verá el mensaje cuando se descongele (si se conecta).");
                    }
                } else {
                    broadcast(notifMsg);
                }

            } catch (Exception e) {
                cliente.enviarMensaje("SISTEMA: Error al guardar mensaje congelado.");
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            cliente.enviarMensaje("SISTEMA: Formato de tiempo inválido.");
        }
    }

    private static void procesarMensajeQuemado(String msg, ManejadorCliente cliente) {
        // Formato: /quemar 10s Mensaje
        try {
            String[] partes = msg.split(" ", 3);
            if (partes.length < 3) {
                cliente.enviarMensaje("SISTEMA: Uso incorrecto. Ejemplo: /quemar 10s Hola");
                return;
            }

            String tiempoStr = partes[1];
            String contenido = partes[2];
            long cantidad = parseTiempo(tiempoStr);
            LocalDateTime fechaCaducidad = calcularFechaFutura(tiempoStr, cantidad);

            String textoSustituto = msg.startsWith("/burn") ? "Burned" : "Quemado";

            try (Session session = Server.sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                session.persist(new Mensaje(getNombreCliente(cliente), contenido, textoSustituto, fechaCaducidad));
                t.commit();

                broadcast(getNombreCliente(cliente) + ": " + contenido);
                cliente.enviarMensaje("SISTEMA: 🔥 Mensaje que se quemará en " + tiempoStr);

            } catch (Exception e) {
                cliente.enviarMensaje("SISTEMA: Error al guardar mensaje quemado.");
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            cliente.enviarMensaje("SISTEMA: Formato de tiempo inválido.");
        }
    }

    private static long parseTiempo(String tiempoStr) {
        return Long.parseLong(tiempoStr.replaceAll("[^0-9]", ""));
    }

    private static LocalDateTime calcularFechaFutura(String tiempoStr, long cantidad) {
        LocalDateTime fecha = LocalDateTime.now();
        if (tiempoStr.endsWith("d")) {
            return fecha.plusDays(cantidad);
        } else if (tiempoStr.endsWith("h")) {
            return fecha.plusHours(cantidad);
        } else if (tiempoStr.endsWith("s")) {
            return fecha.plusSeconds(cantidad);
        } else {
            return fecha.plusMinutes(cantidad);
        }
    }

    private static void broadcast(String mensaje) {
        for (ManejadorCliente c : Server.mapaClientes.values()) {
            c.enviarMensaje(mensaje);
        }
    }

    // Usamos el nuevo método getNombreUsuario de ManejadorCliente
    private static String getNombreCliente(ManejadorCliente cliente) {
        return cliente.getNombreUsuario();
    }
}
