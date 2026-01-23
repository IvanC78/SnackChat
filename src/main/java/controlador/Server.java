package controlador;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.time.LocalDateTime;
import java.util.List;

import modelo.ManejadorCliente;

import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Mapa de clientes conectados
    public static Map<String, ManejadorCliente> mapaClientes = new ConcurrentHashMap<>();

    // Fábrica de conexiones Hibernate (Opción A: Única y estática)
    public static SessionFactory sessionFactory;

    public static void main(String[] args) {
        final int PUERTO = 5000;

        try {
            // 1. Inicializar Hibernate
            iniciarHibernate();

            // 2. Iniciar Servicio de Mensajes Congelados
            java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors
                    .newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try (Session session = sessionFactory.openSession()) {
                    Transaction t = session.beginTransaction();

                    List<modelo.Mensaje> mensajes = session.createQuery(
                            "FROM Mensaje m WHERE m.enviado = false AND m.fechaVisible <= :now", modelo.Mensaje.class)
                            .setParameter("now", LocalDateTime.now())
                            .list();

                    for (modelo.Mensaje m : mensajes) {
                        String contenidoMostrar = "🔓 " + m.getEmisor() + " (Frozen): " + m.getContenido();
                        String dest = m.getDestinatario();

                        if (dest != null) {
                            // Privado
                            ManejadorCliente receptor = mapaClientes.get(dest);
                            ManejadorCliente emisor = mapaClientes.get(m.getEmisor());
                            if (receptor != null)
                                receptor.enviarMensaje(contenidoMostrar);
                            if (emisor != null)
                                emisor.enviarMensaje(contenidoMostrar + " (entregado a " + dest + ")");
                        } else {
                            // Público -> Broadcast
                            for (ManejadorCliente c : mapaClientes.values()) {
                                c.enviarMensaje(contenidoMostrar);
                            }
                        }

                        m.setEnviado(true);
                        session.merge(m);
                    }

                    t.commit();
                } catch (Exception e) {
                    System.err.println("Error procesando mensajes congelados: " + e.getMessage());
                }
            }, 0, 10, java.util.concurrent.TimeUnit.SECONDS);

            // 3. Iniciar Servidor de Sockets
            try (ServerSocket servidor = new ServerSocket(PUERTO)) {
                System.out.println(">>> Servidor Multichat iniciado en puerto " + PUERTO);

                while (true) {
                    Socket socket = servidor.accept();
                    System.out.println("Nueva conexión desde: " + socket.getInetAddress());

                    // Iniciamos el hilo manejador
                    new Thread(new ManejadorCliente(socket)).start();
                }
            }
        } catch (Exception e) {
            System.err.println("Error crítico en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void iniciarHibernate() {
    	System.out.println("Conectando a la base de datos...");
        sessionFactory = new Configuration().configure().buildSessionFactory();
        System.out.println("Base de datos conectada.");
    }
}
