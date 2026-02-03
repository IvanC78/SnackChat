package modelo;

import controlador.Server;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ManejadorCliente implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private Usuario usuario;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    public void enviarMensaje(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            out = new PrintWriter(socket.getOutputStream(), true);

            String linea;
            while ((linea = in.readLine()) != null) {

                if (linea.startsWith("LOGIN ")) {
                    usuario = UsuarioDAO.buscarPorNombre(linea.substring(6));
                    Server.mapaClientes.put(usuario, this);
                }

                else if (linea.startsWith("OPEN_CHAT ")) {
                    Long destinoId = Long.parseLong(linea.substring(10));
                    Chat chat = ChatService.obtenerOCrearChat(usuario.getId(), destinoId);
                    out.println("CHAT_OK " + chat.getId());
                }

                else if (linea.startsWith("SEND ")) {
                    String[] p = linea.split(" ", 3);
                    Long chatId = Long.parseLong(p[1]);
                    String texto = p[2];

                    Chat chat = ChatDAO.obtenerPorId(chatId);
                    guardarMensaje(chat, texto);

                    List<Usuario> miembros = MiembroDAO.obtenerUsuariosPorChat(chatId);
                    for (Usuario u : miembros) {
                        ManejadorCliente mc = Server.mapaClientes.get(u);
                        if (mc != null) {
                            mc.enviarMensaje(usuario.getNombreUsuario() + ": " + texto);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarMensaje(Chat chat, String texto) {
        try (Session s = Server.sessionFactory.openSession()) {
            Transaction t = s.beginTransaction();
            s.persist(new Mensaje(usuario, chat, texto));
            t.commit();
        }
    }
}
