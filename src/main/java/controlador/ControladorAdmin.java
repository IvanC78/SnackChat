package controlador;

import java.io.IOException;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.SesionUsuario;
import modelo.Usuario;
import modelo.UsuarioDAO;
import seguridad.Seguridad;

public class ControladorAdmin {

	@FXML
	ListView<Usuario> panelAdmin;
	@FXML
	TextField textFieldNombre;
	@FXML
	TextField textFieldContrasenya;
	@FXML
	TextField textFieldTelefono;
	@FXML
	TextField textFieldColor;
	@FXML
	Button buttonCrear;
	@FXML
	Button buttonModificar;
	@FXML
	Button buttonEliminar;
	private final ObjectProperty<Usuario> seleccion = new SimpleObjectProperty<>();

	private Alert alerta;

	
	
	public void initialize() {
		cargarUsuarios();
		panelAdmin.setOnMouseClicked(e -> {
		    seleccion.set(panelAdmin.getSelectionModel().getSelectedItem());

		    Usuario u = seleccion.get();
		    if (u != null) {
		        textFieldNombre.setText(u.getNombreUsuario());
		        textFieldContrasenya.setText("");
		        textFieldTelefono.setText(u.getTelefono());
		        textFieldColor.setText(u.getColor());
		    } else {
		        textFieldNombre.setText("");
		        textFieldContrasenya.setText("");
		        textFieldTelefono.setText("");
		        textFieldColor.setText("");
		    }
		});

		buttonCrear.disableProperty().bind(seleccion.isNotNull());
		buttonModificar.disableProperty().bind(seleccion.isNull());
		buttonEliminar.disableProperty().bind(seleccion.isNull());
		alerta = new Alert(Alert.AlertType.INFORMATION);
	}
	
	public void cargarUsuarios(){
		panelAdmin.getItems().clear();
		List<Usuario> usuarios = UsuarioDAO.obtenerTodos();
		panelAdmin.getItems().addAll(usuarios);
		panelAdmin.getItems().add(null);
	}
	
	@FXML
	public void salir(ActionEvent event) throws IOException {

        SesionUsuario.cerrarSesion();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("../vista/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
		
	}
	
	public void crear(ActionEvent e) {
		if(textFieldTelefono.getText().length()==9 && telefonoEsNumerico(textFieldTelefono.getText())) {
			if(textFieldContrasenya.getText().length()>=4){
			Usuario u = new Usuario();
			u.setNombreUsuario(textFieldNombre.getText());
			u.setContrasenya(Seguridad.hashPassword(textFieldContrasenya.getText()));
			u.setColor(textFieldColor.getText());
			u.setTelefono(textFieldTelefono.getText());
			UsuarioDAO.guardarUsuario(u);
	        textFieldNombre.setText("");
	        textFieldContrasenya.setText("");
	        textFieldTelefono.setText("");
	        textFieldColor.setText("");
			cargarUsuarios();
			} else {
				alerta.setHeaderText("ÑAM");
				alerta.setContentText("¡La contraseña debe tener más de 4 dígitos!");
				alerta.showAndWait();
			}
		} else {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡El teléfono debe tener 9 dígitos!");
			alerta.showAndWait();
		}
	}
	public void modificar(ActionEvent e) {
		if(textFieldTelefono.getText().length()==9 && telefonoEsNumerico(textFieldTelefono.getText())) {
			if(textFieldContrasenya.getText().length()>=4 || textFieldContrasenya.getText().equals("")){
				Usuario u = new Usuario();
				u.setId(seleccion.get().getId());
				u.setNombreUsuario(textFieldNombre.getText());
				if(!textFieldContrasenya.getText().equals("")) {
				u.setContrasenya(Seguridad.hashPassword(textFieldContrasenya.getText()));
				} else {
					u.setContrasenya(seleccion.get().getContrasenya());
				}
				u.setColor(textFieldColor.getText());
				u.setTelefono(textFieldTelefono.getText());
				UsuarioDAO.actualizarUsuario(u);
		        textFieldNombre.setText("");
		        textFieldContrasenya.setText("");
		        textFieldTelefono.setText("");
		        textFieldColor.setText("");
				cargarUsuarios();
			} else {
				alerta.setHeaderText("ÑAM");
				alerta.setContentText("¡La contraseña debe tener más de 4 dígitos!");
				alerta.showAndWait();
				}
		} else {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡El teléfono debe tener 9 dígitos!");
			alerta.showAndWait();			
		}
	}
	public void eliminar(ActionEvent e) {
		UsuarioDAO.eliminarUsuario(seleccion.get().getId());
        textFieldNombre.setText("");
        textFieldContrasenya.setText("");
        textFieldTelefono.setText("");
        textFieldColor.setText("");
		cargarUsuarios();
	}
	
	public boolean telefonoEsNumerico(String telefonoS) {
		try {
			Long.parseLong(telefonoS);
			return true;
		} catch (NumberFormatException e) {
			alerta.setHeaderText("ÑAM");
			alerta.setContentText("¡Solo acepta numeros!");
			alerta.showAndWait();
			return false;
		}

	}

}
