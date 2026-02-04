package controlador;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import modelo.ManejadorCliente;
import modelo.ProcesadorMensajesTemporal;
import modelo.Usuario;

import java.net.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Mapa de clientes conectados
    public static Map<Usuario, ManejadorCliente> mapaClientes = new ConcurrentHashMap<>();

    // Fábrica de conexiones Hibernate (Opción A: Única y estática)
    public static SessionFactory sessionFactory;

    public static String SERVER_IP = "localhost";

    public static void main(String[] args) {
        final int PUERTO = 5000;

        try {
            // Por defecto si se arranca el Server.java directo, es modo Servidor Local
            iniciarModoServidor();
        } catch (Exception e) {
            System.err.println("Error crítico en el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ProcesadorMensajesTemporal.detener();
        }
    }

    public static void iniciarModoServidor() {
        SERVER_IP = "localhost";
        iniciarHibernate(SERVER_IP);

        // Arrancar procesador y socket solo si somos host
        ProcesadorMensajesTemporal.iniciar();
        arrancarSocketServer();
    }

    public static void iniciarModoCliente(String ip) {
        SERVER_IP = ip;
        iniciarHibernate(SERVER_IP);
        // Cliente NO arranca socket server ni procesador
    }

    private static void arrancarSocketServer() {
        new Thread(() -> {
            try (ServerSocket servidor = new ServerSocket(5000)) {
                System.out.println(">>> Servidor Sockets iniciado en puerto 5000");
                while (true) {
                    Socket socket = servidor.accept();
                    new Thread(new ManejadorCliente(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void iniciarHibernate(String host) {
        System.out.println("Conectando a la base de datos en: " + host);

        Configuration cfg = new Configuration().configure();

        // Sobrescribir la URL de conexión con la IP elegida
        String url = "jdbc:mysql://" + host
                + ":3306/snackchat?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        cfg.setProperty("hibernate.connection.url", url);

        sessionFactory = cfg.buildSessionFactory();
        System.out.println("Base de datos conectada correctamente.");
    }

    /**
     * Método público para enviar mensaje a todos los clientes conectados
     */
    public static void broadcast(String mensaje) {
        for (ManejadorCliente cliente : mapaClientes.values()) {
            cliente.enviarMensaje(mensaje);
        }
    }
}