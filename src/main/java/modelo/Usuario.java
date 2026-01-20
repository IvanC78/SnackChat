package modelo;

import jakarta.persistence.*;


	@Entity
	@Table(name = "usuarios")
	public class Usuario {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, columnDefinition = "TEXT")
	    private String nombreUsuario;

	    @Column(nullable = false, columnDefinition = "TEXT")
	    private String contrasenya;

	    // falta definir como será admin o usuario normal
	    @Column(nullable = false)
	    private boolean admin;

	    public Usuario() {} //Constructor vacío obligatorio que pide Hibernate

	    public Usuario(String nombreUsuario, String contrasenya, boolean admin) {
	        this.nombreUsuario = nombreUsuario;
	        this.contrasenya = contrasenya;
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
			return nombreUsuario;
		}

		public void setNombreUsuario(String nombreUsuario) {
			this.nombreUsuario = nombreUsuario;
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


