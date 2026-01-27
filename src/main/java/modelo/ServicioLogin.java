package modelo;

import seguridad.Seguridad;

public class ServicioLogin {
	
	public static Usuario validarLogin(String telefono, String constrasenya) {
		
		Usuario usuario = UsuarioDAO.buscarPorTelefono(telefono);
		if (usuario == null) {
			return null;
		}
		
		boolean ok = Seguridad.verificarPassword(constrasenya, usuario.getContrasenya());
		
		if(!ok) {
			return null;
		}
		return usuario;
	}

}
