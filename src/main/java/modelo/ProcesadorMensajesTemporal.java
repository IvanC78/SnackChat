package modelo;

import controlador.Server;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcesadorMensajesTemporal {

    private static ScheduledExecutorService scheduler;

    /**
     * Inicia el procesador que revisa cada segundo si hay mensajes pendientes
     */
    public static void iniciar() {
        if (scheduler != null && !scheduler.isShutdown())
            return;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Ejecutar cada 1 segundo
        scheduler.scheduleAtFixedRate(() -> {
            try {
                procesarMensajesPendientes();
            } catch (Exception e) {
                System.err.println("[Error] Fallo al procesar mensajes temporales: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);

        System.out.println("[Sistema] Procesador de mensajes temporales iniciado.");
    }

    /**
     * Detiene el procesador
     */
    public static void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("[Sistema] Procesador de mensajes temporales detenido.");
        }
    }

    /**
     * Procesa los mensajes que ya cumplieron su tiempo
     */
    private static void procesarMensajesPendientes() {
        List<Mensaje> pendientes = MensajeDAO.obtenerMensajesPendientes();

        for (Mensaje mensaje : pendientes) {
            if ("CONGELADO".equals(mensaje.getTipoTemporal())) {
                // Mostrar mensaje congelado
                String textoMostrar = mensaje.getEmisor() + " (mensaje descongelado): " + mensaje.getContenido();
                Server.broadcast(textoMostrar);
                MensajeDAO.marcarProcesado(mensaje.getId());
                System.out.println("[Sistema] Mensaje congelado mostrado: " + mensaje.getId());

            } else if ("QUEMAR".equals(mensaje.getTipoTemporal())) {
                // Obfuscar mensaje quemado (en lugar de eliminar)
                try (org.hibernate.Session session = Server.sessionFactory.openSession()) {
                    org.hibernate.Transaction t = session.beginTransaction();
                    mensaje.setContenido("🔥 MENSAJE QUEMADO");
                    mensaje.setProcesado(true);
                    session.merge(mensaje);
                    t.commit();
                } catch (Exception e) {
                    System.err.println("Error quemando mensaje: " + e.getMessage());
                }

                String textoEliminar = "🔥 El mensaje de " + mensaje.getEmisor() + " se ha quemado.";
                Server.broadcast(textoEliminar);
                System.out.println("[Sistema] Mensaje quemado obfscado: " + mensaje.getId());
            }
        }
    }
}