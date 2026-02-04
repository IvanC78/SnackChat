package modelo;

import org.hibernate.Session;
import org.hibernate.Transaction;
import controlador.Server;

public class ChatService {

    public static Chat obtenerOCrearChat(Long u1, Long u2) {
        Session s = Server.sessionFactory.openSession();
        try {
            String hql = """
                SELECT c FROM Chat c
                WHERE c.tipo = 'INDIVIDUAL'
                AND c.id IN (
                    SELECT m.chat.id FROM Miembro m
                    WHERE m.usuario.id IN (:u1,:u2)
                    GROUP BY m.chat.id
                    HAVING COUNT(m.chat.id) = 2
                )
            """;

            Chat chat = s.createQuery(hql, Chat.class)
                    .setParameter("u1", u1)
                    .setParameter("u2", u2)
                    .uniqueResult();

            if (chat != null) return chat;

            Transaction t = s.beginTransaction();
            chat = new Chat();
            chat.setTipo(Tipo.INDIVIDUAL);
            chat.setNombre("Privado");
            chat.setColor("#FFFFFF");
            s.persist(chat);

            s.persist(new Miembro(null, chat, UsuarioDAO.obtenerPorId(u1)));
            s.persist(new Miembro(null, chat, UsuarioDAO.obtenerPorId(u2)));

            t.commit();
            return chat;

        } finally {
            s.close();
        }
    }
}
