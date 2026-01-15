package controlador;

import java.io.*;
import java.net.*;
import java.util.*;

import modelo.ManejadorCliente;

public class Server {
    
    
    private static List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        final int PUERTO = 5000;

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println(">>> Servidor multichat iniciado en puerto " + PUERTO);

            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Nueva conexión: " + socket.getInetAddress());

                // Creamos el manejador pasándole el socket y la lista de clientes
                ManejadorCliente manejador = new ManejadorCliente(socket, clientes);
                
                // Iniciamos el hilo para este cliente
                Thread hilo = new Thread(manejador);
                hilo.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
