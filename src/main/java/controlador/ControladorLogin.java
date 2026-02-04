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
import modelo.SesionUsuario;
import modelo.Usuario;
import modelo.UsuarioDAO;
import seguridad.Seguridad;

public class ControladorLogin {

	// LOGIN
	@FXML
	private TextField telefonoLogin;
	@FXML
	private PasswordField contrasenyaLogin;
	@FXML
	private Button buttonLogin;

	// REGISTRO
	@FXML
	private TextField telefonoRegistro;
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
		String telefonoS = telefonoLogin.getText().trim();
		String contrasenyaS = contrasenyaLogin.getText();

		// Validar que no estén vacíos
		if (telefonoS.isEmpty() || contrasenyaS.isEmpty()) {
			mostrarAlerta("Error", "Debes introducir usuario y contraseña", Alert.AlertType.WARNING);
			return;
		}

		telefonoEsValido(telefonoS);

		// Validar contra la BD
		Usuario usuario = ServicioLogin.validarLogin(telefonoS, contrasenyaS);

		if (usuario == null) {
			mostrarAlerta("Error", "Telefono o contraseña incorrectos", Alert.AlertType.WARNING);
			telefonoLogin.clear();
			contrasenyaLogin.clear();
			return;
		}
		SesionUsuario.setUsuarioActual(usuario);

		// Aseguramos que el procesador de mensajes temporales esté corriendo
		// (Por si el usuario no ejecutó Server.java manualmente)
		modelo.ProcesadorMensajesTemporal.iniciar();

		// Si es admin, ir a panel admin
		if (usuario.isAdmin()) {
			cargarPantalla("../vista/panelDeAdmin.fxml", event);
			return;
		}

		// Comprobar si el perfil está incompleto
		boolean perfilIncompleto = usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isEmpty()
				|| usuario.getColor() == null || usuario.getColor().isEmpty();

		if (perfilIncompleto) {
			cargarPantalla("../vista/modificarPerfilInicio.fxml", event);
		} else {
			cargarPantalla("../vista/listadoDeChat.fxml", event);
		}
	}

	// ========== MÉTODO REGISTRO ==========
	@FXML
	public void registro(ActionEvent event) throws IOException {
		String telefonoS = telefonoRegistro.getText().trim();
		String contrasenyaS = contrasenyaRegistro.getText();

		// Validar que no estén vacíos
		if (telefonoS.isEmpty() || contrasenyaS.isEmpty()) {
			mostrarAlerta("Error", "Debes rellenar todos los campos", Alert.AlertType.WARNING);
			return;
		}

		if (telefonoEsValido(telefonoS)) {
			if (contrasenyaS.length() < 4) {
				mostrarAlerta("Error", "La contraseña debe tener al menos 4 caracteres", Alert.AlertType.WARNING);
				return;
			}

			// Comprobar si el usuario ya existe || IMPORTATANTE HEMOS PUESTO TELEFONO EN
			// VEZ DE NOMBRE
			Usuario usuarioExistente = UsuarioDAO.buscarPorTelefono(telefonoS);
			if (usuarioExistente != null) {
				mostrarAlerta("Error", "Este telefono ya existe", Alert.AlertType.WARNING);
				telefonoRegistro.clear();
				return;
			}

			// Detectar si es admin mediante código secreto
			boolean esAdmin = false;
			String contrasenyaReal = contrasenyaS;

			// ~~~~~~~~Si la contraseña termina con "#ADMIN", es un
			// administrador~~~~~~~~~~~~
			if (contrasenyaS.endsWith("#ADMIN")) {
				esAdmin = true;
				contrasenyaReal = contrasenyaS.replace("#ADMIN", ""); // Reemplazamos #ADMIN por nada, así elimino la
																		// clave administrador

				if (contrasenyaReal.length() < 4) {
					mostrarAlerta("Error", "La contraseña (sin código) debe tener al menos 4 caracteres",
							Alert.AlertType.WARNING);
					return;
				}

			}

			// Crear nuevo usuario
			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setTelefono(telefonoS);
			String passwordEncriptada = Seguridad.hashPassword(contrasenyaReal);
			nuevoUsuario.setContrasenya(passwordEncriptada);
			nuevoUsuario.setAdmin(esAdmin);

			// Guardar en BD
			try {
				UsuarioDAO.guardarUsuario(nuevoUsuario);

				String mensajeExito = esAdmin ? "Cuenta de ADMINISTRADOR creada correctamente. Inicia sesión ahora."
						: "Cuenta creada correctamente. Inicia sesión ahora.";
				mostrarAlerta("Éxito", mensajeExito, Alert.AlertType.INFORMATION);

				// Limpiar campos | IMPORTATANTE HEMOS PUESTO TELEFONO EN VEZ DE NOMBRE
				telefonoRegistro.clear();
				contrasenyaRegistro.clear();
				telefonoLogin.setText(telefonoS);
				contrasenyaLogin.clear();
				contrasenyaLogin.requestFocus();

			} catch (Exception e) {
				mostrarAlerta("Error", "Error al crear la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
				e.printStackTrace();
			}
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