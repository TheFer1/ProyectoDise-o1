import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre Formularios
 */
public class FormularioDAO {
    private Connection connection;
    
    public FormularioDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insertar un nuevo formulario
     */
    public boolean insertar(Formulario formulario) {
        String sql = "INSERT INTO formularios(numero_ayudantes, nombre_ayudante, apellido_ayudante, cedula, facultad, estado, id_proyecto, tipoPersonal, periodo_laboral_desde, periodo_laboral_hasta) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, formulario.getNumeroDeAyudantes());
            pstmt.setString(2, formulario.getNombreDelAyudante());
            pstmt.setString(3, formulario.getApellidoDelAyudante());
            pstmt.setString(4, formulario.getCedula());
            pstmt.setString(5, formulario.getFacultad());
            pstmt.setString(6, formulario.getEstado());
            pstmt.setInt(7, formulario.getIdProyecto());
            pstmt.setString(8, formulario.getTipoPersonal());
            pstmt.setString(9, formulario.getPeriodoLaboralDesde());
            pstmt.setString(10, formulario.getPeriodoLaboralHasta());
            pstmt.executeUpdate();
            System.out.println("Formulario insertado para: " + formulario.getNombreDelAyudante());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar formulario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener todos los formularios
     */
    public List<Formulario> obtenerTodos() {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT * FROM formularios ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formulario.setTipoPersonal(rs.getString("tipoPersonal"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener formularios: " + e.getMessage());
        }
        
        return formularios;
    }
    
    /**
     * Obtener formulario por ID
     */
    public Formulario obtenerPorId(int id) {
        String sql = "SELECT * FROM formularios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formulario.setTipoPersonal(rs.getString("tipoPersonal"));
                formulario.setPeriodoLaboralDesde(rs.getString("periodo_laboral_desde"));
                formulario.setPeriodoLaboralHasta(rs.getString("periodo_laboral_hasta"));
                return formulario;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener formulario: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Actualizar formulario
     */
    public boolean actualizar(Formulario formulario) {
        String sql = "UPDATE formularios SET numero_ayudantes = ?, nombre_ayudante = ?, apellido_ayudante = ?, cedula = ?, facultad = ?, estado = ?, id_proyecto = ?, tipoPersonal = ?, periodo_laboral_desde = ?, periodo_laboral_hasta = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, formulario.getNumeroDeAyudantes());
            pstmt.setString(2, formulario.getNombreDelAyudante());
            pstmt.setString(3, formulario.getApellidoDelAyudante());
            pstmt.setString(4, formulario.getCedula());
            pstmt.setString(5, formulario.getFacultad());
            pstmt.setString(6, formulario.getEstado());
            pstmt.setInt(7, formulario.getIdProyecto());
            pstmt.setString(8, formulario.getTipoPersonal());
            pstmt.setString(9, formulario.getPeriodoLaboralDesde());
            pstmt.setString(10, formulario.getPeriodoLaboralHasta());
            pstmt.setInt(11, formulario.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar formulario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar formulario
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM formularios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar formulario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar formularios por estado
     */
    public List<Formulario> buscarPorEstado(String estado) {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT * FROM formularios WHERE estado = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formulario.setTipoPersonal(rs.getString("tipoPersonal"));
                formulario.setPeriodoLaboralDesde(rs.getString("periodo_laboral_desde"));
                formulario.setPeriodoLaboralHasta(rs.getString("periodo_laboral_hasta"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar formularios: " + e.getMessage());
        }
        
        return formularios;
    }
    
    /**
     * Obtener formularios de un usuario específico (por sus proyectos)
     */
    public List<Formulario> obtenerFormulariosPorUsuario(int idUsuario) {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT f.* FROM formularios f " +
                     "INNER JOIN proyectos p ON f.id_proyecto = p.id " +
                     "WHERE p.id_usuario = ? " +
                     "ORDER BY f.id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formulario.setTipoPersonal(rs.getString("tipoPersonal"));
                formulario.setPeriodoLaboralDesde(rs.getString("periodo_laboral_desde"));
                formulario.setPeriodoLaboralHasta(rs.getString("periodo_laboral_hasta"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener formularios del usuario: " + e.getMessage());
        }
        
        return formularios;
    }
    public List<Formulario> buscarPorProyecto(int idProyecto) {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT * FROM formularios WHERE id_proyecto = ? ORDER BY id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idProyecto);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formulario.setTipoPersonal(rs.getString("tipoPersonal"));
                formulario.setPeriodoLaboralDesde(rs.getString("periodo_laboral_desde"));
                formulario.setPeriodoLaboralHasta(rs.getString("periodo_laboral_hasta"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar formularios por proyecto: " + e.getMessage());
        }
        
        return formularios;
    }
    
    /**
     * Cuenta el número de ayudantes registrados en un proyecto
     * @param idProyecto ID del proyecto
     * @return Cantidad de ayudantes (formularios) registrados para ese proyecto
     */
    public int contarAyudantesPorProyecto(int idProyecto) {
        String sql = "SELECT COUNT(*) as total FROM formularios WHERE id_proyecto = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idProyecto);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar ayudantes: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Obtiene un resumen de ayudantes por proyecto CON DESGLOSE POR TIPO DE PERSONAL
     * @return Lista con información detallada de cada proyecto
     */
    public List<Object[]> obtenerResumenAyudantesPorProyecto() {
        List<Object[]> resumen = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.codigo, p.num_ayudantes as requeridos, " +
                     "COUNT(f.id) as registrados, " +
                     "(p.num_ayudantes - COUNT(f.id)) as faltantes, " +
                     "u.nombre as director_nombre, u.apellido as director_apellido " +
                     "FROM proyectos p " +
                     "LEFT JOIN formularios f ON p.id = f.id_proyecto " +
                     "LEFT JOIN usuarios u ON p.id_usuario = u.id " +
                     "GROUP BY p.id, p.nombre, p.codigo, p.num_ayudantes, u.nombre, u.apellido " +
                     "ORDER BY p.nombre";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] fila = new Object[8];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("codigo");
                fila[3] = rs.getInt("requeridos");
                fila[4] = rs.getInt("registrados");
                fila[5] = rs.getInt("faltantes");
                fila[6] = rs.getString("director_nombre") + " " + rs.getString("director_apellido");
                fila[7] = rs.getInt("registrados") >= rs.getInt("requeridos") ? "✓ Completo" : "⚠ Incompleto";
                
                resumen.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener resumen de ayudantes: " + e.getMessage());
        }
        
        return resumen;
    }
    
    /**
     * Obtiene el desglose de ayudantes por tipo de personal en un proyecto
     * @param idProyecto ID del proyecto
     * @return Array con [ayudantesInvestigacion, tecnicos, asistentes]
     */
    public int[] obtenerDesglosePorTipoPersonal(int idProyecto) {
        int[] desglose = {0, 0, 0}; // [Ayudante Investigación, Técnico, Asistente]
        
        String sql = "SELECT tipoPersonal, COUNT(*) as cantidad " +
                     "FROM formularios " +
                     "WHERE id_proyecto = ? " +
                     "GROUP BY tipoPersonal";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idProyecto);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String tipo = rs.getString("tipoPersonal");
                int cantidad = rs.getInt("cantidad");
                
                if (tipo != null) {
                    if (tipo.contains("Ayudante de Investigación")) {
                        desglose[0] += cantidad;
                    } else if (tipo.contains("Técnico")) {
                        desglose[1] += cantidad;
                    } else if (tipo.contains("Asistente")) {
                        desglose[2] += cantidad;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener desglose por tipo: " + e.getMessage());
        }
        
        return desglose;
    }
}
