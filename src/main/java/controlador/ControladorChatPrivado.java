package controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import modelo.Usuario;

public class ControladorChatPrivado {

    @FXML
    private Label labelContacto;

    @FXML
    private VBox contenedorMensajes;

    @FXML
    private TextField campoMensaje;

    private Usuario contacto;

    public void setContacto(Usuario usuario) {
        this.contacto = usuario;
        labelContacto.setText(usuario.getNombreUsuario());
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty()) return;

        // Mensaje propio (derecha)
        agregarMensaje(texto, true);

        campoMensaje.clear();

    }

    public void agregarMensaje(String texto, boolean esMio) {

        HBox fila = new HBox();
        fila.setPadding(new javafx.geometry.Insets(5));

        Label burbuja = new Label(texto);
        burbuja.setWrapText(true);
        burbuja.setMaxWidth(300);
        burbuja.setPadding(new javafx.geometry.Insets(8));

        if (esMio) {
            fila.setAlignment(Pos.CENTER_RIGHT);
            burbuja.setStyle("""
                -fx-background-color: #dcf8c6;
                -fx-background-radius: 10;
            """);
        } else {
            fila.setAlignment(Pos.CENTER_LEFT);
            burbuja.setStyle("""
                -fx-background-color: #ffffff;
                -fx-border-color: #ddd;
                -fx-background-radius: 10;
            """);
        }

        fila.getChildren().add(burbuja);
        contenedorMensajes.getChildren().add(fila);
    }
}
