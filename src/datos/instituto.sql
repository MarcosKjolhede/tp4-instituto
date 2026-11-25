DROP DATABASE IF EXISTS instituto;
CREATE DATABASE instituto;
USE instituto;

CREATE TABLE curso(
    id_curso INT AUTO_INCREMENT,
    codigo VARCHAR(40) NOT NULL,
    idioma VARCHAR(40) NOT NULL,
    nivel VARCHAR(40) NOT NULL,
    turno VARCHAR(40) NOT NULL,
    cupo INT NOT NULL,
    cuota DECIMAL(10,2) NOT NULL,
    vacantes INT NOT NULL,
    CONSTRAINT pk_id_curso PRIMARY KEY (id_curso),
    CONSTRAINT uq_curso_codigo UNIQUE (codigo),
    CONSTRAINT chk_curso_cupo CHECK (cupo > 0),
    CONSTRAINT chk_curso_cuota CHECK (cuota >= 0),
    CONSTRAINT chk_curso_vacantes CHECK (vacantes >= 0 AND vacantes <= cupo)
);

CREATE TABLE alumno(
    id_alumno INT AUTO_INCREMENT,
    dni VARCHAR(10) NOT NULL,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) NOT NULL,
    correo VARCHAR(40),
    CONSTRAINT pk_id_alumno PRIMARY KEY (id_alumno),
    CONSTRAINT uq_alumno_dni UNIQUE (dni)
);

CREATE TABLE inscripcion(
    id_alumno INT NOT NULL,
    id_curso INT NOT NULL,
    fecha DATE NOT NULL DEFAULT(CURRENT_DATE()),
    cuota_paga BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_inscripcion PRIMARY KEY (id_alumno, id_curso),
    CONSTRAINT fk_inscripcion_alumno FOREIGN KEY (id_alumno) REFERENCES alumno(id_alumno),
    CONSTRAINT fk_inscripcion_curso FOREIGN KEY (id_curso) REFERENCES curso(id_curso)
);

-- 1. CURSOS (3 cursos: el curso 3 tiene cupo 3 y 3 inscriptos -> vacantes = 0, completo)
INSERT INTO curso (codigo, idioma, nivel, turno, cupo, cuota, vacantes) VALUES
    ('ING-B1-TM', 'Ingles', 'B1', 'Manana', 20, 45000.00, 15),
    ('POR-A2-TT', 'Portugues', 'A2', 'Tarde', 15, 38000.00, 11),
    ('FRA-A1-TN', 'Frances', 'A1', 'Noche', 3, 42000.00, 0);

-- 2. ALUMNOS (8 alumnos: el alumno 5 tiene correo NULL)
    INSERT INTO alumno (dni, nombre, apellido, correo) VALUES
    ('30111222', 'Lucia', 'Fernandez', 'lucia.fernandez@ejemplo.com'),
    ('31222333', 'Martin', 'Gomez', 'martin.gomez@ejemplo.com'),
    ('32333444', 'Valentina', 'Perez', 'valentina.perez@ejemplo.com'),
    ('33444555', 'Joaquin', 'Rodriguez', 'joaquin.rodriguez@ejemplo.com'),
    ('34555666', 'Camila', 'Lopez', NULL),
    ('35666777', 'Mateo', 'Diaz', 'mateo.diaz@ejemplo.com'),
    ('36777888', 'Sofia', 'Martinez', 'sofia.martinez@ejemplo.com'),
    ('37888999', 'Nicolas', 'Alvarez', 'nicolas.alvarez@ejemplo.com');

-- 3. INSCRIPCIONES (12 inscripciones: 5 en curso 1, 4 en curso 2, 3 en curso 3)
INSERT INTO inscripcion (id_alumno, id_curso, fecha, cuota_paga) VALUES
    -- Curso 1 (ING-B1-TM: cupo 20, 5 inscriptos -> 15 vacantes)
    (1, 1, '2026-11-01', TRUE),
    (2, 1, '2026-11-02', TRUE),
    (3, 1, '2026-11-03', FALSE),
    (4, 1, '2026-11-04', TRUE),
    (5, 1, '2026-11-05', FALSE),

    -- Curso 2 (POR-A2-TT: cupo 15, 4 inscriptos -> 11 vacantes)
    (1, 2, '2026-11-06', TRUE),
    (3, 2, '2026-11-07', FALSE),
    (6, 2, '2026-11-08', TRUE),
    (7, 2, '2026-11-09', FALSE),

    -- Curso 3 (FRA-A1-TN: cupo 3, 3 inscriptos -> 0 vacantes / COMPLETO)
    (2, 3, '2026-11-10', TRUE),
    (5, 3, '2026-11-11', FALSE),
    (8, 3, '2026-11-12', TRUE);

