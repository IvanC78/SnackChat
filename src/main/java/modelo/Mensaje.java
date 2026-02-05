package modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "mensajes")
public class Mensaje {

	public Mensaje(Usuario usuario, Chat chat, String contenido, String tipoTemporal, LocalDateTime fechaAccion) {
		super();
		this.usuario = usuario;
		this.chat = chat;
		this.contenido = contenido;
		this.fechaEnvio = LocalDateTime.now();
		this.tipoTemporal = tipoTemporal;
		this.fechaAccion = fechaAccion;
	}
	
	public Mensaje(Usuario usuario, Chat chat, String contenido) {
		this.usuario = usuario;
		this.chat = chat;
		this.contenido = contenido;
		this.fechaEnvio = LocalDateTime.now();
	}

	public String getTipoTemporal() {
		return tipoTemporal;
	}
	public void setTipoTemporal(String tipoTemporal) {
		this.tipoTemporal = tipoTemporal;
	}
	public LocalDateTime getFechaAccion() {
		return fechaAccion;
	}
	public void setFechaAccion(LocalDateTime fechaAccion) {
		this.fechaAccion = fechaAccion;
	}
	public boolean isProcesado() {
		return procesado;
	}
	public void setProcesado(boolean procesado) {
		this.procesado = procesado;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne
	@JoinColumn(name="usuario_id", referencedColumnName="id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="chat_id", referencedColumnName="id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Chat chat;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    public Mensaje() {} //Constructor vacío obligatorio que pide Hibernate
    
    // Nuevos campos para congelar/quemar
    @Column(name = "tipo_temporal")
    private String tipoTemporal; // "CONGELADO" o "QUEMAR" o null
    
    @Column(name = "fecha_accion")
    private LocalDateTime fechaAccion; // Cuando se debe mostrar/eliminar
    
    @Column(name = "procesado")
    private boolean procesado = false; // Si ya se ejecutó la acción

    

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public String getEmisor() {
		return usuario.getNombreUsuario();
	}
}