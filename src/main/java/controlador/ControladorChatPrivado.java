package controlador;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import modelo.Usuario;
import red.ClienteSocket;

public class ControladorChatPrivado {

    @FXML private Label labelContacto;
    @FXML private VBox contenedorMensajes;
    @FXML private TextField campoMensaje;

    private Long chatId;
    private Usuario contacto;

    public void inicializarChat(Long chatId, Usuario contacto) {
        this.chatId = chatId;
        this.contacto = contacto;
        labelContacto.setText(contacto.getNombreUsuario());

        escucharMensajes();
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty()) return;

        ClienteSocket.getInstancia()
                .enviar("SEND " + chatId + " " + texto);

        agregarMensaje(texto, true);
        campoMensaje.clear();
    }

    private void escucharMensajes() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = ClienteSocket.getInstancia().getIn().readLine()) != null) {
                    String finalMsg = msg;
                    Platform.runLater(() ->
                            agregarMensaje(finalMsg, false)
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void agregarMensaje(String texto, boolean esMio) {
        HBox fila = new HBox();
        fila.setPadding(new javafx.geometry.Insets(5));

        Label burbuja = new Label(texto);
        burbuja.setWrapText(true);
        burbuja.setMaxWidth(300);
        burbuja.setPadding(new javafx.geometry.Insets(8));

        if (esMio) {
            fila.setAlignment(Pos.CENTER_RIGHT);
            burbuja.setStyle("-fx-background-color:#dcf8c6;-fx-background-radius:10;");
        } else {
            fila.setAlignment(Pos.CENTER_LEFT);
            burbuja.setStyle("-fx-background-color:white;-fx-border-color:#ddd;-fx-background-radius:10;");
        }

        fila.getChildren().add(burbuja);
        contenedorMensajes.getChildren().add(fila);
    }
}
