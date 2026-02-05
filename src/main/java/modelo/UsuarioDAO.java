package modelo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import controlador.Server;

public class UsuarioDAO {
	
	/**
	 * Busca un usuario por nombre
	 * @param nombre El nombre del usuario
	 * @return El usuario si existe, null si no existe
	 */
	public static Usuario buscarPorNombre(String nombre) {
		Session session = Server.sessionFactory.openSession();
		try {
			String hql = "FROM Usuario WHERE nombre = :n";
			Query<Usuario> query = session.createQuery(hql, Usuario.class);
			query.setParameter("n", nombre);
			query.setMaxResults(1);
			return query.uniqueResult();
		} catch (Exception e) {
			System.err.println("[Error] No se pudo buscar el usuario: " + e.getMessage());
			return null;
		} finally {
			session.close();
		}
	}
	
	/**
	 * Guarda un nuevo usuario en la base de datos
	 * @param usuario El usuario a guardar
	 */
	public static void guardarUsuario(Usuario usuario) {
		Session sesion = Server.sessionFactory.openSession();
		Transaction transaccion = null;
		try {
			transaccion = sesion.beginTransaction();
			sesion.persist(usuario);
			transaccion.commit();
			System.out.println("[DB] Usuario " + usuario.getNombreUsuario() + " guardado correctamente.");
		} catch (Exception e) {
			if (transaccion != null) {
				transaccion.rollback();
			}
			System.err.println("[Error] No se pudo guardar el usuario: " + e.getMessage());
			throw new RuntimeException("Error al guardar el usuario", e);
		} finally {
			sesion.close();
		}
	}
	
	/**
	 * Actualiza un usuario existente
	 * @param usuario El usuario con los datos actualizados
	 */
	public static void actualizarUsuario(Usuario usuario) {
		Session sesion = Server.sessionFactory.openSession();
		Transaction transaccion = null;
		try {
			transaccion = sesion.beginTransaction();
			sesion.merge(usuario);
			transaccion.commit();
			System.out.println("[DB] Usuario " + usuario.getNombreUsuario() + " actualizado correctamente.");
		} catch (Exception e) {
			if (transaccion != null) {
				transaccion.rollback();
			}
			System.err.println("[Error] No se pudo actualizar el usuario: " + e.getMessage());
			throw new RuntimeException("Error al actualizar el usuario", e);
		} finally {
			sesion.close();
		}
	}
	
	/**
	 * Elimina un usuario de la base de datos
	 * @param usuarioId El ID del usuario a eliminar
	 */
	public static void eliminarUsuario(Long usuarioId) {
		Session sesion = Server.sessionFactory.openSession();
		Transaction transaccion = null;
		try {
			transaccion = sesion.beginTransaction();
			Usuario usuario = sesion.get(Usuario.class, usuarioId);
			if (usuario != null) {
				sesion.remove(usuario);
				transaccion.commit();
				System.out.println("[DB] Usuario eliminado correctamente.");
			} else {
				System.err.println("[Error] Usuario no encontrado.");
			}
		} catch (Exception e) {
			if (transaccion != null) {
				transaccion.rollback();
			}
			System.err.println("[Error] No se pudo eliminar el usuario: " + e.getMessage());
			throw new RuntimeException("Error al eliminar el usuario", e);
		} finally {
			sesion.close();
		}
	}
	
	/**
	 * Obtiene un usuario por su ID
	 * @param usuarioId El ID del usuario
	 * @return El usuario si existe, null si no existe
	 */
	public static Usuario obtenerPorId(Long usuarioId) {
		Session sesion = Server.sessionFactory.openSession();
		try {
			return sesion.get(Usuario.class, usuarioId);
		} catch (Exception e) {
			System.err.println("[Error] No se pudo obtener el usuario: " + e.getMessage());
			return null;
		} finally {
			sesion.close();
		}
	}
	
	public static Usuario buscarPorTelefono(String telefono) {
		Session session = Server.sessionFactory.openSession();
		try {
			String hql = "FROM Usuario WHERE telefono = :t";
			Query<Usuario> query = session.createQuery(hql, Usuario.class);
			query.setParameter("t", telefono);
			query.setMaxResults(1);
			return query.uniqueResult();
		} catch (Exception e) {
			System.err.println("[Error] No se pudo buscar el telefono: " + e.getMessage());
			return null;
		} finally {
			session.close();
		}
	}
	
	
	public static List<Usuario> obtenerTodosMenosActual(Long idActual) {
	    Session session = Server.sessionFactory.openSession();
	    try {
	        String hql = "FROM Usuario WHERE id != :id AND admin != :a";
	        Query<Usuario> query = session.createQuery(hql, Usuario.class);
	        query.setParameter("id", idActual);
	        query.setParameter("a", true);
	        return query.list();
	    } finally {
	        session.close();
	    }
	}
	
	public static List<Usuario> obtenerTodos() {
	    Session session = Server.sessionFactory.openSession();
	    try {
	        String hql = "FROM Usuario WHERE admin != :a";
	        Query<Usuario> query = session.createQuery(hql, Usuario.class);
	        query.setParameter("a", true);
	        return query.list();
	    } finally {
	        session.close();
	    }
	}
}