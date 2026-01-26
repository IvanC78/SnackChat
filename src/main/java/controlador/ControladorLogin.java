package controlador;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
	@FXML
	private Text registro;
	
	Alert alerta = new Alert(Alert.AlertType.WARNING);
	
	@FXML
	public void initialize() {
	    //Cambiar el cursor a una mano al entrar
	    registro.setOnMouseEntered(event -> {
	        registro.setCursor(Cursor.HAND);
	        registro.setFill(Color.BLUE); 
	    });

	    //Volver al estado original al salir
	    registro.setOnMouseExited(event -> {
	        registro.setCursor(Cursor.DEFAULT);
	        registro.setFill(Color.BLACK); 
	    });
	}
	
	@FXML
	public void login(ActionEvent event) throws IOException {
		String nombreS = nombre.getText();
		String contrasenyaS = contrasenya.getText();
		
		Usuario usuario = ServicioLogin.validarLogin(nombreS, contrasenyaS);
		if(nombreS.isEmpty()) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("Debes introducir un nombre de usuario");
			alerta.showAndWait();
			return;
		}
		if(contrasenyaS.isEmpty()) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("Debes introducir una contraseña");
			alerta.showAndWait();
			return;
		}
		
		if(usuario == null) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("Usuario y/o contraseña incorrectos");
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
	@FXML
	public void panelRegistro(MouseEvent event) throws IOException {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("../vista/registro.fxml"));
	    root = loader.load();
	    
	    stage = new Stage();
	    scene = new Scene(root);
	    stage.setScene(scene);
	    stage.show();
	}
}
