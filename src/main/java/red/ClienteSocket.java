package red;

import java.io.*;
import java.net.Socket;

public class ClienteSocket {

    private static ClienteSocket instancia;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private ClienteSocket() {}

    public static ClienteSocket getInstancia() {
        if (instancia == null) {
            instancia = new ClienteSocket();
        }
        return instancia;
    }

    public void conectar(String host, int puerto, String nombreUsuario) throws IOException {
        socket = new Socket(host, puerto);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println("LOGIN " + nombreUsuario);
    }

    public void enviar(String msg) {
        out.println(msg);
    }

    public BufferedReader getIn() {
        return in;
    }
}
