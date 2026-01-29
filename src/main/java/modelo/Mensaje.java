package modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
public class Mensaje {
    public Mensaje(Long id, Usuario usuario, Chat chat, String contenido, LocalDateTime fechaEnvio) {
		this.id = id;
		this.usuario = usuario;
		this.chat = chat;
		this.contenido = contenido;
		this.fechaEnvio = fechaEnvio;
	}
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne
	@JoinColumn(name="usuario_id", referencedColumnName="id", nullable=false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="chat_id", referencedColumnName="id", nullable=false)
	private Chat chat;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    public Mensaje() {} //Constructor vacío obligatorio que pide Hibernate

    

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
}
