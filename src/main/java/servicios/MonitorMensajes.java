package servicios;

import modelo.ManejadorCliente;
import modelo.Mensaje;
import controlador.Server;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDateTime;
import java.util.List;

public class MonitorMensajes implements Runnable {

    // Runs every 5 seconds
    private static final int CHECK_INTERVAL_MS = 5000;

    @Override
    public void run() {
        while (true) {
            try {
                enviarMensajesCongelados();
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                System.out.println("MonitorMensajes detenido.");
                break;
            } catch (Exception e) {
                System.err.println("Error en MonitorMensajes: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void enviarMensajesCongelados() {
        if (Server.sessionFactory == null)
            return;

        try (Session session = Server.sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Buscar mensajes NO enviados y cuya fecha visible ya pasó
            String hql = "FROM Mensaje m WHERE m.enviado = false AND m.fechaVisible <= :now";
            List<Mensaje> mensajesListos = session.createQuery(hql, Mensaje.class)
                    .setParameter("now", LocalDateTime.now())
                    .getResultList();

            for (Mensaje m : mensajesListos) {
                // Marcar como enviado primero para evitar re-envíos en caso de error
                m.setEnviado(true);
                session.merge(m);

                String textoFinal = "❄️ DESCONGELADO [" + m.getEmisor() + "]: " + m.getContenido();

                if (m.getDestinatario() != null) {
                    // Es un mensaje privado
                    ManejadorCliente destinatario = Server.mapaClientes.get(m.getDestinatario());
                    ManejadorCliente emisor = Server.mapaClientes.get(m.getEmisor());

                    if (destinatario != null) {
                        destinatario.enviarMensaje(textoFinal);
                    }
                    if (emisor != null) {
                        emisor.enviarMensaje("❄️ Tu mensaje para " + m.getDestinatario() + " se ha descongelado.");
                    }
                } else {
                    // Es un mensaje global
                    broadcast(textoFinal);
                }

                System.out.println("Mensaje descongelado ID: " + m.getId());
            }

            t.commit();
        } catch (Exception e) {
            System.err.println("Error procesando mensajes congelados: " + e.getMessage());
        }
    }

    private void broadcast(String mensaje) {
        for (ManejadorCliente c : Server.mapaClientes.values()) {
            c.enviarMensaje(mensaje);
        }
    }
}
