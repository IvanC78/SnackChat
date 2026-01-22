package controlador;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelo.ServicioLogin;
import modelo.Usuario;

public class ControladorLogin {

	@FXML
	private Button login;
	@FXML
	private TextField nombre;
	@FXML
	private TextField contrasenya;
	@FXML
	private Pane panel;
	private Parent root;
	private Stage stage;
	private Scene scene;
	
	Alert alerta = new Alert(Alert.AlertType.WARNING);
	
	@FXML
	public void login(ActionEvent event) throws IOException {
		String nombreS = nombre.getText();
		String contrasenyaS = contrasenya.getText();
		
		Usuario usuario = ServicioLogin.validarLogin(nombreS, contrasenyaS);
		
		if(usuario == null) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("Debes introducir un usuario");
			alerta.showAndWait();
			return;
		}
		
		if(usuario.isAdmin()) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../vista/panelDeAdmin.fxml"));
			root = loader.load();
			
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			}else {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("../vista/listadoDeChat.fxml"));
				root = loader.load();
				
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
		}		
	}
}
