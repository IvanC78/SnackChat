package controlador;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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

            // 2. Iniciar Servidor de Sockets
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
