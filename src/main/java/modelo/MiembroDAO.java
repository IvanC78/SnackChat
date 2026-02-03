package modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;
import controlador.Server;
import java.util.List;

public class MiembroDAO {

    public static List<Usuario> obtenerUsuariosPorChat(Long chatId) {
        Session session = Server.sessionFactory.openSession();
        try {
            String hql = "SELECT m.usuario FROM Miembro m WHERE m.chat.id = :id";
            Query<Usuario> q = session.createQuery(hql, Usuario.class);
            q.setParameter("id", chatId);
            return q.list();
        } finally {
            session.close();
        }
    }
}
