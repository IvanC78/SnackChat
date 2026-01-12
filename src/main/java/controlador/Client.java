package controlador;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final String IP_SERVIDOR = "localhost"; // Cambia a la IP del servidor si no es local
        final int PUERTO = 5000;
        
        System.out.print("Introduce tu nombre de usuario: ");
        Scanner sc = new Scanner(System.in);
        String nombreUsuario = sc.nextLine();

        try (Socket socket = new Socket(IP_SERVIDOR, PUERTO)) {
            System.out.println("Conectado al chat. Escribe 'chau' para salir.");

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Hilo para RECIBIR mensajes del servidor
            Thread recibir = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = br.readLine()) != null) {
                        System.out.println("\n" + msg);
                        System.out.print("> "); // Prompt visual
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada.");
                }
            });

            // Hilo para ENVIAR mensajes al servidor
            Thread enviar = new Thread(() -> {
                try {
                    while (true) {
                        String texto = sc.nextLine();
                        if (texto.equalsIgnoreCase("chau")) {
                            socket.close();
                            break;
                        }
                        out.println(nombreUsuario + ": " + texto);
                    }
                } catch (Exception e) {
                    System.out.println("Error al enviar.");
                }
            });

            recibir.start();
            enviar.start();

            // Esperar a que el hilo de envío termine para cerrar el programa
            enviar.join();
        } catch (Exception e) {
            System.out.println("Error: No se pudo conectar al servidor.");
        }
    }
}



