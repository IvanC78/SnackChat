package modelo;

import java.io.*;
import java.net.*;
import java.util.List;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private List<PrintWriter> clientes;
    private PrintWriter out;

    public ManejadorCliente(Socket socket, List<PrintWriter> clientes) {
        this.socket = socket;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try {
            // Configuramos la entrada y salida para ESTE cliente concreto
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Agregamos este cliente al chat global para que reciba mensajes
            clientes.add(out);

            String mensaje;
            // Bucle que escucha mensajes de ESTE cliente
            while ((mensaje = in.readLine()) != null) {
                System.out.println("Mensaje recibido: " + mensaje);
                retransmitir(mensaje);
            }
        } catch (IOException e) {
            System.out.println("Un cliente ha abandonado el chat.");
        } finally {
            // Cuando el cliente se desconecta, lo quitamos de la lista y cerramos socket
            if (out != null) {
                clientes.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar el mensaje a todos los clientes conectados
    private void retransmitir(String mensaje) {
        synchronized (clientes) {               // synchronized gestionar los mensajes simultáneos, genera concurrencia, comparten mismo recurso pero no se pisan
            for (PrintWriter cliente : clientes) {
                cliente.println(mensaje);
            }
        }
    }
}

