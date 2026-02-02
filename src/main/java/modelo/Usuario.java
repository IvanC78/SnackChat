package modelo;

import jakarta.persistence.*;


	@Entity
	@Table(name = "usuarios")
	public class Usuario {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = true, columnDefinition = "TEXT")
	    private String nombre;

	    @Column(nullable = false, columnDefinition = "TEXT")
	    private String contrasenya;
	    
	    @Column(nullable = false, unique = true, length = 9) 
	    private String telefono; 
	    
	    @Column(nullable = true, columnDefinition = "TEXT") 
	    private String color;
	    

	    // falta definir como será admin o usuario normal
	    @Column(nullable = false)
	    private boolean admin;

	    public Usuario() {} //Constructor vacío obligatorio que pide Hibernate

	    public Usuario(String nombre, String contrasenya, String telefono, String color, boolean admin) {
	        this.nombre = nombre;
	        this.contrasenya = contrasenya;
	        this.telefono = telefono;
	        this.color = color;
	        this.admin = admin;
	        // si da tiempo, añadir hora para saber a que hora se ha logueado la people
	    }

	    // Getters y Setters
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNombreUsuario() {
			return nombre;
		}

		public void setNombreUsuario(String nombre) {
			this.nombre = nombre;
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

		public String getTelefono() {
			return telefono;
		}

		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		@Override
		public String toString() {
			return nombre;
		}
		
		
	}