package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import modelo.ServicioLogin;
import modelo.Usuario;

public class ControladorLogin {

	@FXML
	private Button login;
	@FXML
	private TextField nombre;
	@FXML
	private TextField contrasenya;
	
	
	public void login(ActionEvent event) {
		String nombreS = nombre.getText();
		String contrasenyaS = contrasenya.getText();
		
		Usuario usuario = ServicioLogin.validarLogin(nombreS, contrasenyaS);
		
		if(usuario == null) {
			//pop-up debes introducir usuario
			return;
		}
		
		if(usuario.isAdmin()) {
			//hacia la ventana admin
		}else {
			//hacia la ventana usuario
		}		
	}
}
