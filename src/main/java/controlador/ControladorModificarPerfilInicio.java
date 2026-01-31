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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modelo.ServicioRegistro;

public class ControladorModificarPerfilInicio {

	@FXML
	private TextField nombreUsuario;
	@FXML
	private ColorPicker colorUsuario;
	@FXML
	private Button finalizarPerfil;
	
	Alert alerta = new Alert(Alert.AlertType.WARNING);
	Alert alertaError = new Alert(Alert.AlertType.ERROR);
	Alert alertaCorrecta = new Alert(Alert.AlertType.CONFIRMATION);
	
	public void continuar(ActionEvent event) throws IOException {
		String nombreUsuarioS = nombreUsuario.getText();
		Color colorC = colorUsuario.getValue();
		
		
		if(nombreUsuarioS.isEmpty()) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡Debes rellenar el nombre!");
			alerta.showAndWait();
			return;
		}
		
		ServicioRegistro.completarPerfil(nombreUsuarioS,colorC.toString());
		alertaCorrecta.setHeaderText("Registro completado"); 
		alertaCorrecta.setContentText("Usuario registrado correctamente"); 
		alertaCorrecta.showAndWait();
		cambiarPestanya("../vista/listadoDeChat.fxml", event);
	}
	
	
	public void cambiarPestanya(String fxml, ActionEvent event) throws IOException {
	    
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../vista/listadoDeChat.fxml"));
		Parent root = loader.load();
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	    
	}
}
