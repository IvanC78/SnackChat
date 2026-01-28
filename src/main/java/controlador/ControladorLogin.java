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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.ServicioLogin;
import modelo.Usuario;
import modelo.UsuarioDAO;
import seguridad.Seguridad;

public class ControladorLogin {
	
	// LOGIN
	@FXML
	private TextField nombreLogin;
	@FXML
	private PasswordField contrasenyaLogin;
	@FXML
	private Button buttonLogin;
	
	// REGISTRO
	@FXML
	private TextField nombreRegistro;
	@FXML
	private PasswordField contrasenyaRegistro;
	@FXML
	private Button buttonRegistro;
	
	private Alert alerta;
	
	@FXML
	public void initialize() {
		alerta = new Alert(Alert.AlertType.INFORMATION);
	}

	// ========== MÉTODO LOGIN ==========
	@FXML
	public void login(ActionEvent event) throws IOException {
		String nombreS = nombreLogin.getText().trim();
		String contrasenyaS = contrasenyaLogin.getText();
		
		// Validar que no estén vacíos
		if (nombreS.isEmpty() || contrasenyaS.isEmpty()) {
			mostrarAlerta("Error", "Debes introducir usuario y contraseña", Alert.AlertType.WARNING);
			return;
		}
		
		// Validar contra la BD
		Usuario usuario = ServicioLogin.validarLogin(nombreS, contrasenyaS);
		
		if (usuario == null) {
			mostrarAlerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.WARNING);
			nombreLogin.clear();
			contrasenyaLogin.clear();
			return;
		}
		
		// Si es admin, ir a panel admin
		if (usuario.isAdmin()) {
			cargarPantalla("../vista/panelDeAdmin.fxml", event);
		} else {
			// Si es usuario normal, ir a chat general
			cargarPantalla("../vista/listadoDeChat.fxml", event);
		}
	}

	// ========== MÉTODO REGISTRO ==========
	@FXML
	public void registro(ActionEvent event) throws IOException {
		String nombreS = nombreRegistro.getText().trim();
		String contrasenyaS = contrasenyaRegistro.getText();
		
		// Validar que no estén vacíos
		if (nombreS.isEmpty() || contrasenyaS.isEmpty()) {
			mostrarAlerta("Error", "Debes rellenar todos los campos", Alert.AlertType.WARNING);
			return;
		}
		
		// Validar longitud mínima
		if (nombreS.length() < 3) {
			mostrarAlerta("Error", "El nombre debe tener al menos 3 caracteres", Alert.AlertType.WARNING);
			return;
		}
		
		if (contrasenyaS.length() < 4) {
			mostrarAlerta("Error", "La contraseña debe tener al menos 4 caracteres", Alert.AlertType.WARNING);
			return;
		}
		
		// Comprobar si el usuario ya existe
		Usuario usuarioExistente = UsuarioDAO.buscarPorNombre(nombreS);
		if (usuarioExistente != null) {
			mostrarAlerta("Error", "Este nombre de usuario ya existe", Alert.AlertType.WARNING);
			nombreRegistro.clear();
			return;
		}
		
		// Crear nuevo usuario (siempre como usuario normal, no admin)
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombreUsuario(nombreS);
		String passwordEncriptada = Seguridad.hashPassword(contrasenyaS);
		nuevoUsuario.setContrasenya(passwordEncriptada);
		nuevoUsuario.setAdmin(false); // Los nuevos registros son usuarios normales
		
		// Guardar en BD
		try {
			UsuarioDAO.guardarUsuario(nuevoUsuario);
			mostrarAlerta("Éxito", "Cuenta creada correctamente. Inicia sesión ahora.", Alert.AlertType.INFORMATION);
			
			// Limpiar campos
			nombreRegistro.clear();
			contrasenyaRegistro.clear();
			nombreLogin.setText(nombreS);
			contrasenyaLogin.clear();
			contrasenyaLogin.requestFocus();
			
		} catch (Exception e) {
			mostrarAlerta("Error", "Error al crear la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	// ========== MÉTODOS AUXILIARES ==========
	private void cargarPantalla(String fxml, ActionEvent event) throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
			Parent root = loader.load();
			
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			mostrarAlerta("Error", "No se pudo cargar la pantalla: " + e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}
	
	private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
		Alert alerta = new Alert(tipo);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}
}