package controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import modelo.Usuario;

public class ControladorChatPrivado {

    @FXML
    private Label labelIcono;

    @FXML
    private Label labelContacto;

    @FXML
    private VBox contenedorMensajes;

    @FXML
    private TextField campoMensaje;

    private Usuario contacto;

    public void initialize() {
        // Iniciar polling para actualizar el chat cada segundo
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> actualizarMensajes()));
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private modelo.Chat obtenerChatActual() {
        if (contacto == null)
            return null;
        Usuario yo = modelo.SesionUsuario.getUsuarioActual();
        if (yo == null)
            return null;
        return modelo.ChatDAO.obtenerChatPrivado(yo, contacto);
    }

    private void actualizarMensajes() {
        if (contacto == null)
            return;

        try (org.hibernate.Session session = controlador.Server.sessionFactory.openSession()) {

            modelo.Chat chatActual = obtenerChatActual();
            if (chatActual == null)
                return;

            java.util.List<modelo.Mensaje> mensajes = session
                    .createQuery("FROM Mensaje m WHERE m.chat.id = :chatId", modelo.Mensaje.class)
                    .setParameter("chatId", chatActual.getId())
                    .list();

            contenedorMensajes.getChildren().clear();

            Usuario yo = modelo.SesionUsuario.getUsuarioActual();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

            for (modelo.Mensaje m : mensajes) {
                boolean esMio = m.getUsuario().getId().equals(yo.getId());
                String contenido = m.getContenido();
                String estilo = "NORMAL";

                // Lógica visual
                if ("CONGELADO".equals(m.getTipoTemporal())) {
                    estilo = "AZUL"; // Siempre azulado si es/fue congelado
                    if (!m.isProcesado()) {
                        String hora = m.getFechaAccion() != null ? m.getFechaAccion().format(formatter) : "breves";
                        // Reescrito limpio para evitar caracteres extraños
                        contenido = "❄️ Mensaje congelado (disponible a las " + hora + ")";
                    }
                } else if ("QUEMAR".equals(m.getTipoTemporal())) {
                    if (m.isProcesado()) {
                        estilo = "CENIZA"; // Ya quemado
                    } else {
                        estilo = "FUEGO"; // A punto de quemarse
                        String hora = m.getFechaAccion() != null ? m.getFechaAccion().format(formatter) : "breves";
                        contenido = contenido + " (🔥 " + hora + ")";
                    }
                }

                agregarMensaje(contenido, esMio, estilo);
            }

        } catch (Exception e) {
            System.err.println("Error actualizando chat: " + e.getMessage());
        }
    }

    public void setContacto(Usuario usuario) {
        this.contacto = usuario;
        labelContacto.setText(usuario.getNombreUsuario());

        // Teñir icono con el color del usuario
        String colorHex = usuario.getColor();
        if (colorHex == null || !colorHex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            colorHex = "#4a4a4a"; // Default dark grey
        }
        if (labelIcono != null) {
            labelIcono.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 24px;");
        }

        actualizarMensajes();
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty())
            return;

        // Detectar si es comando
        if (texto.startsWith("/congelar ")) {
            procesarCongelar(texto);
            campoMensaje.clear();
            return;
        } else if (texto.startsWith("/quemar ")) {
            procesarQuemar(texto);
            campoMensaje.clear();
            return;
        }

        // Mensaje normal
        agregarMensaje(texto, true, "NORMAL");

        // Guardar en BD (para que persista si no se usa Sockets)
        Usuario emisor = modelo.SesionUsuario.getUsuarioActual();
        modelo.Chat chat = obtenerChatActual(); // USAR CHAT PRIVADO
        if (emisor != null && chat != null) {
            guardarEnBD(emisor, chat, texto);
        }

        campoMensaje.clear();
    }

    // Copia de la logica de ManejadorCliente para procesar comandos localmente en
    // la UI
    private void procesarCongelar(String comando) {
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^/congelar\\s+(\\d+)([smhd])\\s+(.+)$");
            java.util.regex.Matcher matcher = pattern.matcher(comando);

            if (!matcher.matches()) {
                agregarMensaje("❌ Formato incorrecto. Uso: /congelar 10s Tu mensaje aquí", false, "NORMAL");
                return;
            }

            int cantidad = Integer.parseInt(matcher.group(1));
            String unidad = matcher.group(2);
            String mensaje = matcher.group(3);

            java.time.LocalDateTime fechaAccion = calcularFechaAccion(cantidad, unidad);

            if (fechaAccion == null) {
                agregarMensaje("❌ Tiempo inválido. Rango: 1s a 30d", false, "NORMAL");
                return;
            }

            Usuario emisor = modelo.SesionUsuario.getUsuarioActual();
            modelo.Chat chat = obtenerChatActual(); // USAR CHAT PRIVADO

            if (emisor != null && chat != null) {
                guardarMensajeCongelado(emisor, chat, mensaje, fechaAccion);
                String tiempoTexto = cantidad + unidad;
                agregarMensaje("❄️ Mensaje congelado para enviarse en " + tiempoTexto, true, "AZUL");
            }

        } catch (Exception e) {
            agregarMensaje("❌ Error al procesar comando: " + e.getMessage(), false, "NORMAL");
        }
    }

    private void procesarQuemar(String comando) {
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^/quemar\\s+(\\d+)([smhd])\\s+(.+)$");
            java.util.regex.Matcher matcher = pattern.matcher(comando);

            if (!matcher.matches()) {
                agregarMensaje("❌ Formato incorrecto. Uso: /quemar 5m Tu mensaje aquí", false, "NORMAL");
                return;
            }

            int cantidad = Integer.parseInt(matcher.group(1));
            String unidad = matcher.group(2);
            String mensaje = matcher.group(3);

            java.time.LocalDateTime fechaAccion = calcularFechaAccion(cantidad, unidad);

            if (fechaAccion == null) {
                agregarMensaje("❌ Tiempo inválido. Rango: 1s a 30d", false, "NORMAL");
                return;
            }

            Usuario emisor = modelo.SesionUsuario.getUsuarioActual();
            modelo.Chat chat = obtenerChatActual(); // USAR CHAT PRIVADO

            if (emisor != null && chat != null) {
                guardarMensajeQuemar(emisor, chat, mensaje, fechaAccion);
                String tiempoTexto = cantidad + unidad;
                agregarMensaje("🔥 Mensaje autodestructible enviado (" + tiempoTexto + "): " + mensaje, true, "FUEGO");
            }

        } catch (Exception e) {
            agregarMensaje("❌ Error al procesar comando: " + e.getMessage(), false, "NORMAL");
        }
    }

    private java.time.LocalDateTime calcularFechaAccion(int cantidad, String unidad) {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        switch (unidad) {
            case "s":
                if (cantidad < 1 || cantidad > 2592000)
                    return null;
                return ahora.plusSeconds(cantidad);
            case "m":
                if (cantidad < 1 || cantidad > 43200)
                    return null;
                return ahora.plusMinutes(cantidad);
            case "h":
                if (cantidad < 1 || cantidad > 720)
                    return null;
                return ahora.plusHours(cantidad);
            case "d":
                if (cantidad < 1 || cantidad > 30)
                    return null;
                return ahora.plusDays(cantidad);
            default:
                return null;
        }
    }

    // Métodos de guardado directos (reutilizando Hibernate del Server/Main)

    private void guardarEnBD(Usuario emisor, modelo.Chat chat, String texto) {
        try (org.hibernate.Session session = controlador.Server.sessionFactory.openSession()) {
            org.hibernate.Transaction t = session.beginTransaction();
            session.persist(new modelo.Mensaje(emisor, chat, texto));
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarMensajeCongelado(Usuario usuario, modelo.Chat chat, String texto,
            java.time.LocalDateTime fechaAccion) {
        try (org.hibernate.Session session = controlador.Server.sessionFactory.openSession()) {
            org.hibernate.Transaction t = session.beginTransaction();
            modelo.Mensaje mensaje = new modelo.Mensaje(usuario, chat, texto, "CONGELADO", fechaAccion);
            session.persist(mensaje);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarMensajeQuemar(Usuario usuario, modelo.Chat chat, String texto,
            java.time.LocalDateTime fechaAccion) {
        try (org.hibernate.Session session = controlador.Server.sessionFactory.openSession()) {
            org.hibernate.Transaction t = session.beginTransaction();
            modelo.Mensaje mensaje = new modelo.Mensaje(usuario, chat, texto, "QUEMAR", fechaAccion);
            session.persist(mensaje);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agregarMensaje(String texto, boolean esMio, String estilo) {
        HBox fila = new HBox();
        fila.setPadding(new javafx.geometry.Insets(2, 5, 2, 5)); // Reduce vertical gap

        Label burbuja = new Label(texto);
        burbuja.setWrapText(true);
        burbuja.setMaxWidth(300);
        burbuja.setPadding(new javafx.geometry.Insets(8, 12, 8, 12)); // More padding for nicer text
        burbuja.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 13px;");

        // Estilos base
        String radius = "15";
        String styleBase = "-fx-background-radius: " + radius + "; -fx-border-radius: " + radius + "; ";
        String colorFondo = "#ffffff";
        String colorBorde = "-fx-border-color: #e0e0e0; -fx-border-width: 1;";
        String colorTexto = "-fx-text-fill: #333333;";

        if (esMio) {
            colorFondo = "#dcf8c6"; // Greenish match with typical apps
            colorBorde = "-fx-border-color: transparent;";
        }

        // Overrides por estilo especial
        switch (estilo) {
            case "AZUL":
                // Congelado: Azul suave
                colorFondo = esMio ? "#bbdefb" : "#e3f2fd";
                colorBorde = "-fx-border-color: #90caf9;";
                colorTexto = "-fx-text-fill: #0d47a1;";
                break;
            case "FUEGO":
                // Quemar pendiente: Naranja suave
                colorFondo = esMio ? "#ffccbc" : "#ffe0b2";
                colorBorde = "-fx-border-color: #ffab91;";
                colorTexto = "-fx-text-fill: #bf360c;";
                break;
            case "CENIZA":
                // Quemado: Gris oscuro
                colorFondo = "#546e7a";
                colorTexto = "-fx-text-fill: #eceff1; -fx-font-style: italic;";
                colorBorde = "-fx-border-color: #455a64;";
                break;
        }

        burbuja.setStyle(burbuja.getStyle() + styleBase +
                "-fx-background-color: " + colorFondo + "; " +
                colorBorde +
                colorTexto);

        // Sombras suaves para dar profundidad
        burbuja.setEffect(new javafx.scene.effect.DropShadow(3, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));

        if (esMio) {
            fila.setAlignment(Pos.CENTER_RIGHT);
        } else {
            fila.setAlignment(Pos.CENTER_LEFT);
        }

        fila.getChildren().add(burbuja);
        contenedorMensajes.getChildren().add(fila);
    }
}
