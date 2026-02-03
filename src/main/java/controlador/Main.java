package controlador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Usuario;
import modelo.UsuarioDAO;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource("../vista/login.fxml"));
			Scene scene = new Scene(root, 600, 500);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Server.iniciarHibernate();

			// Crear usuarios de prueba si no existen
			crearUsuarioSiNoExiste("Bocata", "111111111", "1234");
			crearUsuarioSiNoExiste("Patata", "222222222", "1234");

		} catch (Exception e) {
			System.err.println("Error al inicializar: " + e.getMessage());
		}

		launch(args);
	}

	private static void crearUsuarioSiNoExiste(String nombre, String telefono, String password) {
		if (UsuarioDAO.buscarPorNombre(nombre) == null) {
			Usuario u = new Usuario();
			u.setNombreUsuario(nombre);
			u.setTelefono(telefono);
			String passwordEncriptada = seguridad.Seguridad.hashPassword(password);
			u.setContrasenya(passwordEncriptada);
			u.setAdmin(false);
			UsuarioDAO.guardarUsuario(u);
			System.out.println("Usuario " + nombre + " creado.");
		} else {
			System.out.println("El usuario " + nombre + " ya existe, saltando creación.");
		}
	}

}
