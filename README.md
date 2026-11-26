-------------------------------------------------------
-------------------------------------------------------

# TP N4 INSTITUTO DE IDIOMAS

# UTN Extension áulica Necochea

# PROGRAMACION II

# PROFESOR: AUGUSTO CESAR FERNANDEZ CARBONELL

# ALUMNOS: ACOSTA Marcos, AREVALO Bautista, CEPEDA Ezequiel

------------------------------------------------------
------------------------------------------------------

## 1. ¿Qué pasa si dos personas usan el programa al mismo tiempo y se inscriben al último lugar de un curso?¿Qué hace su implementación?


    **Si dos usuarios intentan ocupar la última vacante simultaneamente, el sistema evita la sobreinscripcion gracias a que el método inscribir() de InscripcionDAO ejecuta una transaccion que comienza con un "UPDATE curso SET vacantes = vacantes - 1 WHERE id_curso = ? AND vacantes > 0"
    Esta sentencia (en InnoDB) toma un bloqueo a nivel de fila sobre el registro del curso hasta que finaliza la transaccion: la primera petición descuenta la última vacante (1 a 0) y confirma con commit(), mientras que la segunda queda en espera y, al ejecutarse, encuentra vacantes = 0, afecta 0 filas y lanza una InstitutoException informando que está completo, sin dejar datos inconsistentes.**


## 2. Muestren una consulta de su programa escrita con PreparedStatement y expliquen qué le pasaría si la hubieran armado concatenando lo que escribe el usuario.


        `public void insertar(Alumno a) throws InstitutoException{
        String sql = "INSERT INTO alumno (nombre, apellido, dni, correo) VALUES (?, ?, ?, ?)";
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, a.getNombre());
                ps.setString(2, a.getApellido());
                ps.setString(3, a.getDni());
                ps.setString(4, a.getCorreo());//null entonces null en la base
                ps.executeUpdate();
                try(ResultSet claves = ps.getGeneratedKeys()){
                    if (claves.next()){
                        a.setId_alumno(claves.getInt(1));
                    }
                }
            }catch(SQLException e){
                throw TraductorDeErrores.traducir(e);
            }
    }`

    **Si hubieramos armado concatenando strings, la aplicacion seria vulnerable a ataques de inyección SQL, permitiendo que un usuario por error o malintencionado altere la estructura de la consulta mediente comillas simples para leer, modificar o borrar tablas enteras. Ademas cualquier apellido con apostrofe x'xxx romperia la sintaxis SQL provocando un error de ejecucion en la base de datos.**


## 3. ¿Dónde decidieron que viva la regla del cupo: en la ventana, en el acceso a datos o en la base? Justifiquen la decisión.


    **Decidimos que la regla del cupo viva principalmente en la base de datos mediante restricciones (CONSTRAINT chk_curso_vacantes CHECK (vacantes >= 0 AND vacantes <= cupo) y CHECK (cupo > 0)), respaldada en la capa de acceso a datos por la actualización condicional (WHERE vacantes > 0) dentro de una transacción en InscripcionDAO. Como la regla está en la base de datos, los datos siguen bien aunque dos personas entren a la vez, haya más de una ventana abierta o alguien toque la base directamente; en cambio, si la validación dependiera únicamente de la ventana en Java, dos terminales leyendo el mismo estado en memoria podrían superponer inscripciones y violar el cupo máximo.**


