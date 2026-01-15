package modelo;

import modelo.Mensaje;
import org.hibernate.Session;
import org.hibernate.Transaction;

import controlador.Server;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private String nombreUsuario;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    public void enviarMensaje(String msg) {
        if (out != null) out.println(msg);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);

            // Registro inicial: El primer mensaje del cliente es su nombre
            this.nombreUsuario = in.readLine();
            Server.mapaClientes.put(nombreUsuario, this);
            //broadcast("SISTEMA: " + nombreUsuario + " se ha unido al chat.");  FRONTEND LUZ VERDE CONECTADO

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("@")) {
                    enviarPrivado(msg);
                } else {
                    // Guardar en MySQL
                    guardarEnBD(nombreUsuario, msg);
                    // Retransmitir a todos
                    broadcast(nombreUsuario + ": " + msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Conexión perdida con " + nombreUsuario);
        } finally {
            if (nombreUsuario != null) {
                Server.mapaClientes.remove(nombreUsuario);
                //broadcast("SISTEMA: " + nombreUsuario + " ha salido.");  FRONTEND LUZ ROJA DESCONECTADO
            }
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void guardarEnBD(String emisor, String texto) {
        // Usamos la fábrica estática del Server
        try (Session session = Server.sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(new Mensaje(emisor, texto));
            t.commit();
            System.out.println("[DB] Mensaje de " + emisor + " guardado.");
        } catch (Exception e) {
            System.err.println("[Error DB] No se pudo guardar: " + e.getMessage());
        }
    }

    private void enviarPrivado(String msgCompleto) {
        int primerEspacio = msgCompleto.indexOf(" ");
        if (primerEspacio != -1) {
            String destino = msgCompleto.substring(1, primerEspacio);
            String contenido = msgCompleto.substring(primerEspacio + 1);

            ManejadorCliente receptor = Server.mapaClientes.get(destino);
            if (receptor != null) {
                receptor.enviarMensaje("(Privado de " + nombreUsuario + "): " + contenido);
                this.enviarMensaje("(Privado para " + destino + "): " + contenido);
            } else {
                this.enviarMensaje("SISTEMA: Usuario " + destino + " no encontrado.");
            }
        }
    }

    private void broadcast(String mensaje) {
        for (ManejadorCliente c : Server.mapaClientes.values()) {
            c.enviarMensaje(mensaje);
        }
    }
}
