package modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import controlador.Server;

public class UsuarioDAO {
	
	public static Usuario buscarPorNombre(String nombre) {
		Session sesion = Server.sessionFactory.openSession();
		sesion.beginTransaction();
		
		Query<Usuario> query =sesion.createQuery("FROM Usuarios WHERE nombre = :nombre", Usuario.class);
		query.setParameter("nombre", nombre);
		
		Usuario usuario = query.uniqueResult();
		
		sesion.getTransaction();
		sesion.close();
		
		return usuario;	
	}

	public static void guardarUsuario(Usuario usuario) {
		Session sesion = Server.sessionFactory.openSession();
		sesion.beginTransaction();
		
		sesion.persist(usuario);
		
		sesion.getTransaction().commit();
		sesion.close();
	}
}
