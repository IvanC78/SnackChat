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
			Scene scene = new Scene(root,400,400);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public static void main(String[] args) {
	    try {
	        Server.iniciarHibernate();
	        
	        // Solo guardamos si NO existe ya
	        if (UsuarioDAO.buscarPorNombre("snackchat") == null) {
	            Usuario u = new Usuario();
	            u.setNombre_usuario("snackchat");
	            String passwordEncriptada = seguridad.Seguridad.hashPassword("1234");
	            u.setContrasenya(passwordEncriptada);
	            u.setAdmin(true);    
	            UsuarioDAO.guardarUsuario(u);
	            System.out.println("Usuario administrador creado.");
	        } else {
	            System.out.println("El usuario administrador ya existe, saltando creación.");
	        }
	        
	    } catch(Exception e) {
	        System.err.println("Error al inicializar: " + e.getMessage());
	    }
	    
	    launch(args);
	}

}
