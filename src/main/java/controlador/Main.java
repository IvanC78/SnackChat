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

			// Silenciar logs de Hibernate agresivamente
			java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

			// Crear usuarios de prueba si no existen
			// Crear usuarios de prueba si no existen
			crearUsuarioSiNoExiste("Bocata", "111111111", "1234", "#FFA500");
			crearUsuarioSiNoExiste("Patata", "222222222", "1234", "#8B4513");

			// Asegurar que existe el Chat General (ID 1)
			if (modelo.ChatDAO.obtenerPorId(1L) == null) {
				modelo.Chat chatGeneral = new modelo.Chat();
				chatGeneral.setNombre("General");
				chatGeneral.setColor("#FFFFFF");
				chatGeneral.setTipo(modelo.Tipo.GRUPAL);
				modelo.ChatDAO.guardarChat(chatGeneral);
				System.out.println("Chat General creado (ID asignado por BD).");
			}

		} catch (Exception e) {
			System.err.println("Error al inicializar: " + e.getMessage());
		}

		launch(args);
	}

	private static void crearUsuarioSiNoExiste(String nombre, String telefono, String password, String color) {
		Usuario u = UsuarioDAO.buscarPorNombre(nombre);
		if (u == null) {
			u = new Usuario();
			u.setNombreUsuario(nombre);
			u.setTelefono(telefono);
			String passwordEncriptada = seguridad.Seguridad.hashPassword(password);
			u.setContrasenya(passwordEncriptada);
			u.setColor(color);
			u.setAdmin(false);
			UsuarioDAO.guardarUsuario(u);
			System.out.println("Usuario " + nombre + " creado.");
		} else {
			// Actualizar color si no tiene o es diferente (para corregir usuarios antiguos)
			if (u.getColor() == null || !u.getColor().equals(color)) {
				u.setColor(color);
				UsuarioDAO.actualizarUsuario(u);
				System.out.println("Usuario " + nombre + " actualizado con color " + color);
			} else {
				System.out.println("El usuario " + nombre + " ya existe y tiene color correcto.");
			}
		}
	}

}
