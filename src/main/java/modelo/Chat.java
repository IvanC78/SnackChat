package modelo;

import jakarta.persistence.*;

@Entity
@Table(name="chat")
public class Chat {
	public Chat(Long id, Tipo tipo, String nombre, String color) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.nombre = nombre;
		this.color = color;
	}

	public Chat() {}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nombre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String color;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
}
