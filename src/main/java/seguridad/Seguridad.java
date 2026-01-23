package seguridad;

import org.mindrot.jbcrypt.BCrypt;

public class Seguridad {
	
	public static String hashPassword(String contrasenya) {
		return BCrypt.hashpw(contrasenya, BCrypt.gensalt(12));
	}
	
	public static boolean verificarPassword(String contrasenya, String hash) {
		return BCrypt.checkpw(contrasenya, hash);
	}

}
