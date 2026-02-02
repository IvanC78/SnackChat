package modelo;

import jakarta.persistence.*;


@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String nombre_usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contrasenya;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String color;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String telefono;
    
    // falta definir como será admin o usuario normal
    @Column(nullable = false)
    private boolean admin;

    public Usuario() {} //Constructor vacío obligatorio que pide Hibernate

    public Usuario(String nombre_usuario, String contrasenya, String color, String telefono, boolean admin) {
        this.nombre_usuario = nombre_usuario;
        this.contrasenya = contrasenya;
        this.color = color;
        this.telefono = telefono;
        this.admin = admin;
        // si da tiempo, añadir hora para saber a que hora se ha logueado la people
    }

    public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getNombreUsuario() {
		return nombre_usuario;
	}

	public void setNombreUsuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}

	// Getters y Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContrasenya() {
		return contrasenya;
	}

	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
}