package modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import controlador.Server;

public class UsuarioDAO {
	
	public static Usuario buscarPorNombre(String nombre) {
	    Session session = Server.sessionFactory.openSession();
	    try {
	        String hql = "FROM Usuario WHERE nombre = :n";
	        return session.createQuery(hql, Usuario.class)
	                      .setParameter("n", nombre)
	                      .setMaxResults(1) // <-- Esto fuerza a que solo traiga uno aunque haya varios usuarios, así al crear cada vez la BD no da error
	                      .uniqueResult(); 
	    } finally {
	        session.close();
	    }
	}


	public static void guardarUsuario(Usuario usuario) {
		Session sesion = Server.sessionFactory.openSession();
		sesion.beginTransaction();
		
		sesion.persist(usuario);
		
		sesion.getTransaction().commit();
		sesion.close();
	}
}
