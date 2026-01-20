package controlador;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final String IP_SERVIDOR = "192.168.72.47"; // Cambia a la IP del servidor si no es local
        final int PUERTO = 5000;

        try (Socket socket = new Socket(IP_SERVIDOR, PUERTO)) {
            System.out.println("Conectado al chat. Escribe 'chau' para salir.");

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true);
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
            System.out.print("Añade de tu nombre: ");
            Scanner sc = new Scanner(System.in);
            String nombreUsuario = sc.nextLine();
            out.println(nombreUsuario);
            System.out.print("> "); // Prompt visual

            // Hilo para ENVIAR mensajes al servidor
            Thread enviar = new Thread(() -> {
                try {
                    while (true) {

                        String texto = sc.nextLine();
                        // if (texto.equalsIgnoreCase("chau")) {
                        // socket.close();
                        // break;
                        // }
                        // out.println(nombreUsuario+": ");
                        out.println(texto);
                    }
                } catch (Exception e) {
                    System.out.println("Error al enviar.");
                }
            });

            recibir.start();
            enviar.start();

            // Esperar a que el hilo de envío termine para cerrar el programa
            // Esperar a que el hilo de envío termine para cerrar el programa
            enviar.join();
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: No se pudo conectar al servidor.");
        }
    }
}
