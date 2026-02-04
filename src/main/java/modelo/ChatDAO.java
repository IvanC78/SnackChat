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

	public static Chat obtenerChatGeneral() {
		org.hibernate.Session sesion = Server.sessionFactory.openSession();
		try {
			String hql = "FROM Chat c WHERE c.nombre = :nombre";
			Chat chat = sesion.createQuery(hql, Chat.class)
					.setParameter("nombre", "General")
					.uniqueResult();

			if (chat == null) {
				// Crear si no existe
				org.hibernate.Transaction t = sesion.beginTransaction();
				chat = new Chat();
				chat.setNombre("General");
				chat.setColor("#FFFFFF");
				chat.setTipo(Tipo.GRUPAL);
				sesion.persist(chat);
				t.commit();
				System.out.println("Chat General creado automáticamente en DAO.");
			}
			return chat;
		} catch (Exception e) {
			System.err.println("Error obteniendo chat general: " + e.getMessage());
			return null;
		} finally {
			sesion.close();
		}
	}

	public static void guardarChat(Chat chat) {
		org.hibernate.Session sesion = Server.sessionFactory.openSession();
		org.hibernate.Transaction t = null;
		try {
			t = sesion.beginTransaction();
			sesion.persist(chat);
			t.commit();
		} catch (Exception e) {
			if (t != null)
				t.rollback();
			e.printStackTrace();
		} finally {
			sesion.close();
		}
	}

	public static Chat obtenerChatPrivado(Usuario u1, Usuario u2) {
		org.hibernate.Session sesion = Server.sessionFactory.openSession();
		try {
			Long id1 = u1.getId();
			Long id2 = u2.getId();

			String nombreChat;
			if (id1 < id2) {
				nombreChat = "PRIV_" + id1 + "_" + id2;
			} else {
				nombreChat = "PRIV_" + id2 + "_" + id1;
			}

			String hql = "FROM Chat c WHERE c.nombre = :nombre";
			Chat chat = sesion.createQuery(hql, Chat.class)
					.setParameter("nombre", nombreChat)
					.uniqueResult();

			if (chat == null) {
				org.hibernate.Transaction t = sesion.beginTransaction();
				chat = new Chat();
				chat.setNombre(nombreChat);
				chat.setColor("#FFFFFF");
				chat.setTipo(Tipo.INDIVIDUAL);
				sesion.persist(chat);
				t.commit();
			}
			return chat;
		} catch (Exception e) {
			System.err.println("Error obteniendo chat privado: " + e.getMessage());
			return null;
		} finally {
			sesion.close();
		}
	}
}