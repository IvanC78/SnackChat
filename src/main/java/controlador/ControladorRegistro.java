package controlador;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modelo.ServicioRegistro;

public class ControladorRegistro {

	@FXML
	private TextField usuario;
	@FXML
	private PasswordField contrasenya;
	@FXML
	private PasswordField segundacontrasenya;
	@FXML
	private ColorPicker color;
	@FXML
	private TextField telefono;
	@FXML
	private Button registrarse;
	@FXML
	private Pane panel;
	private Parent root;
	private Stage stage;
	private Scene scene;
	
	
	Alert alerta = new Alert(Alert.AlertType.WARNING);
	Alert alertaError = new Alert(Alert.AlertType.ERROR);
	Alert alertaCorrecta = new Alert(Alert.AlertType.CONFIRMATION);
	
	@FXML
	public void registro(ActionEvent event) throws IOException{
		String usuarioS = usuario.getText();
		String contrasenyaS = contrasenya.getText(); 
		String contrasenya2S = segundacontrasenya.getText();
		Color colorC = color.getValue();
		String telefonoS = telefono.getText();
		
		if(usuarioS.isEmpty() || contrasenyaS.isEmpty() || contrasenya2S.isEmpty() || telefonoS.isEmpty()) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡Debes rellenar todos los campos!");
			alerta.showAndWait();
			return;
		}
		
		System.out.println(contrasenyaS +" "+contrasenyaS);
		if(!contrasenyaS.equals(contrasenya2S)) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡Las contraseñas deben coincidir!");
			alerta.showAndWait();
			return;
		}
		
		telefonoEsNumerico(telefonoS);
		telefonoEsValido(telefonoS);
		
		if (!ServicioRegistro.telefonoDisponible(telefonoS)) { 
			alertaError.setHeaderText("Teléfono duplicado"); 
			alertaError.setContentText("¡Este teléfono ya esta registrado!"); 
			alertaError.showAndWait(); 
			return; 
		}
		
		ServicioRegistro.registrar(usuarioS, contrasenyaS, telefonoS, colorC.toString());
		alertaCorrecta.setHeaderText("Registro completado"); 
		alertaCorrecta.setContentText("Usuario registrado correctamente"); 
		alertaCorrecta.showAndWait();
		volver();
		
		
	}
	
	public boolean telefonoEsNumerico(String telefonoS) {
		try {
			Integer.parseInt(telefonoS);
			return true;
		}catch(NumberFormatException e) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡Solo acepta numeros!");
			alerta.showAndWait();
			return false;
		}
	}
	
	public boolean telefonoEsValido(String telefonoS) {
	    if (!telefonoEsNumerico(telefonoS)) {
	        return false;
	    }

	    if (telefonoS.length() != 9) {
	        alerta.setHeaderText("ÑAM");
	        alerta.setContentText("¡El teléfono debe tener 9 dígitos!");
	        alerta.showAndWait();
	        return false;
	    }

	    return true;
	}
	
	public void volver() throws IOException {
	    
	    Stage stage = (Stage) registrarse.getScene().getWindow(); 
	    stage.close();
	    
	}

	
}
