package controlador;

import java.net.InetAddress;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorModoConexion {

    @FXML
    private Button btnHost;
    @FXML
    private Button btnCliente;
    @FXML
    private TextField campoIP;
    @FXML
    private Label mensajeEstado;

    @FXML
    private void modoAnfitrion(ActionEvent event) {
        try {
            // 1. Iniciar Servidor apuntando a localhost (es el anfitrión)
            String localIP = obtenerIPLocal();
            String hostname = obtenerHostname();

            // Inicia Hibernate apuntando a local y arranca el Server Socket
            Server.iniciarModoServidor();

            // Crear datos base (solo el Host puede hacerlo con seguridad)
            Main.asegurarDatosDePrueba();

            // Mostrar IP al usuario para que la comparta
            mostrarAlertaInfo("Modo Anfitrión Iniciado",
                    "¡Servidor listo!\n\n" +
                            "IP: " + localIP + "\n" +
                            "Hostname: " + hostname + "\n\n" +
                            "Comparte cualquiera de los dos con tus compañeros.");

            irALogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error al iniciar servidor", e.getMessage());
        }
    }

    @FXML
    private void modoCliente(ActionEvent event) {
        String ipDestino = campoIP.getText().trim();

        if (ipDestino.isEmpty()) {
            mensajeEstado.setText("¡Por favor, escribe una IP!");
            return;
        }

        try {
            mensajeEstado.setText("Conectando...");

            // Inicia Hibernate apuntando a la IP destino
            Server.iniciarModoCliente(ipDestino);

            irALogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error de conexión",
                    "No se pudo conectar a la IP: " + ipDestino
                            + "\nVerifica que:\n1. La IP sea correcta.\n2. El anfitrión haya iniciado 'SER ANFITRÓN'.\n3. Estéis en la misma WiFi.\n4. El Firewall no lo bloquee.");
            mensajeEstado.setText("Error al conectar.");
        }
    }

    private void irALogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../vista/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private String obtenerIPLocal() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Desconocida";
        }
    }

    private String obtenerHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Desconocido";
        }
    }

    private void mostrarAlertaInfo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SnackChat - " + titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
