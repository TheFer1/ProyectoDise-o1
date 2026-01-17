/**
 * Clase base Usuario que representa un usuario del sistema
 */
public class Usuario {
    protected int id;
    protected String nombre;
    protected String apellido;
    protected String correo;
    protected String contraseña;
    protected String tipo; // Director, Jefatura, Usuario
    
    // Constructores
    public Usuario() {
    }
    
    public Usuario(String nombre, String apellido, String correo, String contraseña) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contraseña = contraseña;
    }
    
    public Usuario(int id, String nombre, String apellido, String correo, String contraseña) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contraseña = contraseña;
    }
    
    public Usuario(int id, String nombre, String apellido, String correo, String contraseña, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contraseña = contraseña;
        this.tipo = tipo;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getContraseña() {
        return contraseña;
    }
    
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
    
    // Alias para getPassword() (usado en login)
    public String getPassword() {
        return contraseña;
    }
    
    public void setPassword(String password) {
        this.contraseña = password;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     */
    public boolean registrarUsuario() {
        // Lógica de registro
        return true;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
