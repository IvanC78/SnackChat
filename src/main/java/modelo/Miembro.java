package modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "miembros")
public class Miembro {

	public Miembro(Long id, Chat chat, Usuario usuario) {
		this.id = id;
		this.chat = chat;
		this.usuario = usuario;
	}
	
	public Miembro() {}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name="chat_id", referencedColumnName="id", nullable=false)
	private Chat chat;
	
	@ManyToOne
	@JoinColumn(name="usuario_id", referencedColumnName="id", nullable=false)
	private Usuario usuario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
