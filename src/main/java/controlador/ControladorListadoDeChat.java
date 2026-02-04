package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import modelo.SesionUsuario;
import modelo.Usuario;
import modelo.UsuarioDAO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControladorListadoDeChat implements Initializable {

    @FXML
    private Label labelNombreUsuario;

    @FXML
    private ListView<Usuario> contactos;

    @FXML
    private Button buttonExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Usuario actual = SesionUsuario.getUsuarioActual();
        labelNombreUsuario.setText(actual.getNombreUsuario());

        cargarUsuarios();

        contactos.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Usuario seleccionado = contactos.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    abrirChatPrivado(seleccionado);
                }
            }
        });
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = UsuarioDAO.obtenerTodosMenosActual(
                SesionUsuario.getUsuarioActual().getId());
        contactos.getItems().setAll(usuarios);

        contactos.setCellFactory(lv -> new javafx.scene.control.ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(usuario.getNombreUsuario());

                    // Círculo de color
                    javafx.scene.shape.Circle colorCircle = new javafx.scene.shape.Circle(8);
                    String colorHex = usuario.getColor();
                    if (colorHex == null || !colorHex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                        colorHex = "#cccccc"; // Default gris
                    }
                    try {
                        colorCircle.setFill(javafx.scene.paint.Color.web(colorHex));
                    } catch (Exception e) {
                        colorCircle.setFill(javafx.scene.paint.Color.GRAY);
                    }
                    setGraphic(colorCircle);
                    setGraphicTextGap(10);
                    setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-padding: 5;");
                }
            }
        });
    }

    private void abrirChatPrivado(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("../vista/chatPrivado.fxml"));
            Parent root = loader.load();

            ControladorChatPrivado controller = loader.getController();
            controller.setContacto(usuario);

            Stage stage = new Stage();
            stage.setTitle("Chat con " + usuario.getNombreUsuario());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void salir(ActionEvent event) throws IOException {

        SesionUsuario.cerrarSesion();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("../vista/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
