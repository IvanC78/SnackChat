package modelo;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import controlador.Server;
import java.time.LocalDateTime;
import java.util.List;

public class MensajeDAO {
    
    /**
     * Obtiene mensajes pendientes de procesar (congelados que deben mostrarse o quemar que deben eliminarse)
     */
    public static List<Mensaje> obtenerMensajesPendientes() {
        Session session = Server.sessionFactory.openSession();
        try {
            String hql = "FROM Mensaje WHERE procesado = false AND fechaAccion <= :ahora AND tipoTemporal IS NOT NULL";
            Query<Mensaje> query = session.createQuery(hql, Mensaje.class);
            query.setParameter("ahora", LocalDateTime.now());
            return query.list();
        } catch (Exception e) {
            System.err.println("[Error] No se pudieron obtener mensajes pendientes: " + e.getMessage());
            return List.of();
        } finally {
            session.close();
        }
    }
    
    /**
     * Marca un mensaje como procesado
     */
    public static void marcarProcesado(Long mensajeId) {
        Session session = Server.sessionFactory.openSession();
        Transaction transaccion = null;
        try {
            transaccion = session.beginTransaction();
            Mensaje mensaje = session.get(Mensaje.class, mensajeId);
            if (mensaje != null) {
                mensaje.setProcesado(true);
                // No es necesario merge cuando el objeto ya está en la sesión
            }
            transaccion.commit();
        } catch (Exception e) {
            if (transaccion != null) {
                transaccion.rollback();
            }
            System.err.println("[Error] No se pudo marcar mensaje como procesado: " + e.getMessage());
        } finally {
            session.close();
        }
    }
    
    /**
     * Elimina un mensaje de la base de datos
     */
    public static void eliminarMensaje(Long mensajeId) {
        Session session = Server.sessionFactory.openSession();
        Transaction transaccion = null;
        try {
            transaccion = session.beginTransaction();
            Mensaje mensaje = session.get(Mensaje.class, mensajeId);
            if (mensaje != null) {
                session.remove(mensaje);
                transaccion.commit();
                System.out.println("[DB] Mensaje quemado eliminado.");
            }
        } catch (Exception e) {
            if (transaccion != null) {
                transaccion.rollback();
            }
            System.err.println("[Error] No se pudo eliminar mensaje: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}