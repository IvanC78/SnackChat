package modelo;

import seguridad.Seguridad;

public class ServicioRegistro {
	
	  	public static boolean telefonoDisponible(String telefono) {
	        return UsuarioDAO.buscarPorTelefono(telefono) == null;
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


