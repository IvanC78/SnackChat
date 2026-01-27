package controlador;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
		
		ServicioRegistro.registrar(usuarioS, contrasenyaS, telefonoS, colorC.toString());
		alertaCorrecta.setHeaderText("Registro completado"); 
		alertaCorrecta.setContentText("Usuario registrado correctamente"); 
		alertaCorrecta.showAndWait();
		// HAY QUE HACER QUE LA PESTAÑA SE CIERRE CUANDO LE DAS A ACEPTAR
		
		
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

	
}
