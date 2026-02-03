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
import red.ClienteSocket;

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
                SesionUsuario.getUsuarioActual().getId()
        );
        contactos.getItems().setAll(usuarios);
    }

    private void abrirChatPrivado(Usuario usuario) {
        try {
            ClienteSocket socket = ClienteSocket.getInstancia();
            socket.enviar("OPEN_CHAT " + usuario.getId());

            String respuesta = socket.getIn().readLine(); // CHAT_OK id
            Long chatId = Long.parseLong(respuesta.split(" ")[1]);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("../vista/chatPrivado.fxml")
            );
            Parent root = loader.load();

            ControladorChatPrivado controller = loader.getController();
            controller.inicializarChat(chatId, usuario);

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
                getClass().getResource("../vista/login.fxml")
        );
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
