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
    
    // Nuevos campos para congelar/quemar
    @Column(name = "tipo_temporal")
    private String tipoTemporal; // "CONGELADO" o "QUEMAR" o null
    
    @Column(name = "fecha_accion")
    private LocalDateTime fechaAccion; // Cuando se debe mostrar/eliminar
    
    @Column(name = "procesado")
    private boolean procesado = false; // Si ya se ejecutó la acción

    public Mensaje() {} // Constructor vacío obligatorio que pide Hibernate

    public Mensaje(String emisor, String contenido) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.fechaEnvio = LocalDateTime.now();
    }
    
    public Mensaje(String emisor, String contenido, String tipoTemporal, LocalDateTime fechaAccion) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.fechaEnvio = LocalDateTime.now();
        this.tipoTemporal = tipoTemporal;
        this.fechaAccion = fechaAccion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public String getTipoTemporal() { return tipoTemporal; }
    public void setTipoTemporal(String tipoTemporal) { this.tipoTemporal = tipoTemporal; }
    public LocalDateTime getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(LocalDateTime fechaAccion) { this.fechaAccion = fechaAccion; }
    public boolean isProcesado() { return procesado; }
    public void setProcesado(boolean procesado) { this.procesado = procesado; }
}