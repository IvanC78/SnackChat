package modelo;

import org.hibernate.Session;

import controlador.Server;

public class ChatDAO {

	public static Chat obtenerPorId(Long chatId) {
		Session sesion = Server.sessionFactory.openSession();
		try {
			return sesion.get(Chat.class, chatId);
		} catch (Exception e) {
			System.err.println("[Error] No se pudo obtener el chat: " + e.getMessage());
			return null;
		} finally {
			sesion.close();
		}
	}
	
}
