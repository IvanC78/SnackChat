package modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emisor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_visible")
    private LocalDateTime fechaVisible;

    @Column(name = "fecha_caducidad")
    private LocalDateTime fechaCaducidad;

    @Column(name = "texto_sustituto")
    private String textoSustituto;

    @Column(name = "destinatario")
    private String destinatario;

    @Column(nullable = false)
    private boolean enviado = true;

    public Mensaje() {
    } // Constructor vacío obligatorio que pide Hibernate

    // Constructor normal
    public Mensaje(String emisor, String contenido) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.fechaEnvio = LocalDateTime.now();
        this.enviado = true;
    }

    // Constructor para mensajes CONGELADOS
    public Mensaje(String emisor, String contenido, LocalDateTime fechaVisible, String destinatario) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.fechaEnvio = LocalDateTime.now();
        this.fechaVisible = fechaVisible;
        this.destinatario = destinatario;
        this.enviado = false; // No se envía inmediatamente
    }

    // Constructor para mensajes QUEMADOS (Burn)
    public Mensaje(String emisor, String contenido, String textoSustituto, LocalDateTime fechaCaducidad) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.fechaEnvio = LocalDateTime.now();
        this.textoSustituto = textoSustituto;
        this.fechaCaducidad = fechaCaducidad;
        this.enviado = true; // Se envía inmediatamente (se quema después)
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public LocalDateTime getFechaVisible() {
        return fechaVisible;
    }

    public void setFechaVisible(LocalDateTime fechaVisible) {
        this.fechaVisible = fechaVisible;
    }

    public LocalDateTime getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDateTime fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getTextoSustituto() {
        return textoSustituto;
    }

    public void setTextoSustituto(String textoSustituto) {
        this.textoSustituto = textoSustituto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }
}
