package controlador;

import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Server {
   public static void main(String[] args) {
       final int PUERTO = 5000;
       try (ServerSocket servidor = new ServerSocket(PUERTO)) {
           System.out.println("Servidor iniciado. Esperando cliente...");
           Socket socket = servidor.accept();
           System.out.println("Cliente conectado desde: " + socket.getInetAddress());
           // IMPORTANTE: El segundo parámetro 'true' activa el auto-flush
           PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
           BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           Scanner sc = new Scanner(System.in);
           Thread recibir = new Thread(() -> {
               try {
                   String msg;
                   // readLine() espera hasta encontrar un \n
                   while ((msg = br.readLine()) != null) {
                       System.out.println(msg);
                      
                   }
               } catch (IOException e) {
                   System.out.println("\n[Error] Conexión perdida durante la lectura.");
               }
           });
           Thread enviar = new Thread(() -> {
               try {
                   while (!socket.isClosed()) {
                      
                       if (sc.hasNextLine()) {
                           String texto = sc.nextLine();
                           // Usamos println para asegurar el salto de línea \n
                           out.println(texto);
                           if (texto.equalsIgnoreCase("chau")) break;
                       }
                   }
               } catch (Exception e) {
                   System.out.println("\n[Error] Error al enviar mensaje.");
               }
           });
           recibir.start();
           enviar.start();
          
           enviar.join();
           socket.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}

