package modelo;

import seguridad.Seguridad;

public class ServicioRegistro {
	
	  	public static boolean telefonoDisponible(String telefono) {
	        return UsuarioDAO.buscarPorTelefono(telefono) == null;
	    }
	  	
	  	public static void registrar(String telefono,String contrasenya) {
	        Usuario u = new Usuario();
	        u.setContrasenya(Seguridad.hashPassword(contrasenya));
	        u.setTelefono(telefono);


	        UsuarioDAO.guardarUsuario(u);
	    }
	  	public static void completarPerfil(String nombre, String color) {

	        Usuario u = SesionUsuario.getUsuarioActual();

	        if (u == null) {
	            throw new IllegalStateException("No hay usuario en sesión");
	        }

	        u.setNombreUsuario(nombre);
	        u.setColor(color);

	        UsuarioDAO.actualizarUsuario(u);
	    }

	    public static void registrar(String nombre, String contrasenya, String telefono, String color) {
	        Usuario u = new Usuario();
	        u.setNombreUsuario(nombre);
	        u.setContrasenya(Seguridad.hashPassword(contrasenya));
	        u.setTelefono(telefono);
	        u.setColor(color);

	        UsuarioDAO.guardarUsuario(u);
	    }
	}


