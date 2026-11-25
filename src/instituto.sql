DROP DATABASE IF EXISTS instituto;
CREATE DATABASE instituto;
USE instituto;

CREATE TABLE curso (
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

CREATE TABLE alumno (
    id_alumno INT AUTO_INCREMENT,
    dni VARCHAR(10) NOT NULL,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) NOT NULL,
    correo VARCHAR(40),
    CONSTRAINT pk_id_alumno PRIMARY KEY (id_alumno),
    CONSTRAINT uq_alumno_dni UNIQUE (dni)
);

CREATE TABLE inscripcion (
    id INT AUTO_INCREMENT,
    id_alumno INT NOT NULL,
    id_curso INT NOT NULL,
    fecha DATE NOT NULL DEFAULT (CURRENT_DATE()),
    cuota_paga BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_inscripcion PRIMARY KEY (id),
    CONSTRAINT uq_inscripcion_alumno_curso UNIQUE (id_alumno, id_curso),
    CONSTRAINT fk_inscripcion_alumno FOREIGN KEY (id_alumno) REFERENCES alumno(id_alumno),
    CONSTRAINT fk_inscripcion_curso FOREIGN KEY (id_curso) REFERENCES curso(id_curso)
);

-- Datos de prueba: 3 cursos (id_curso 1, 2, 3), 8 alumnos (id_alumno 1..8), 12 inscripciones
-- ING-A1-M (id_curso = 1): cupo 3, 3 inscriptos -> 0 vacantes
-- FRA-A1-T (id_curso = 2): cupo 6, 5 inscriptos -> 1 vacante
-- POR-B1-N (id_curso = 3): cupo 8, 4 inscriptos -> 4 vacantes

INSERT INTO curso (codigo, idioma, nivel, turno, cupo, cuota, vacantes) VALUES
  ('ING-A1-M', 'Ingles',    'A1', 'Maniana', 3, 18000.00, 0),
  ('FRA-A1-T', 'Frances',   'A1', 'Tarde',   6, 17500.00, 1),
  ('POR-B1-N', 'Portugues', 'B1', 'Noche',   8, 16000.00, 4);

INSERT INTO alumno (id_alumno, dni, nombre, apellido, correo) VALUES
  (1, '30111222', 'Lucia',     'Gomez',   'lucia.gomez@mail.com'),
  (2, '31222333', 'Martin',    'Perez',   'martin.perez@mail.com'),
  (3, '32333444', 'Sofia',     'Ramirez', 'sofia.ramirez@mail.com'),
  (4, '33444555', 'Tomas',     'Acosta',  NULL),
  (5, '34555666', 'Valentina', 'Diaz',    'vale.diaz@mail.com'),
  (6, '35666777', 'Joaquin',   'Sosa',    'joaquin.sosa@mail.com'),
  (7, '36777888', 'Camila',    'Torres',  'camila.torres@mail.com'),
  (8, '37888999', 'Nicolas',   'Benitez', 'nico.benitez@mail.com');

INSERT INTO inscripcion (id_alumno, id_curso, fecha, cuota_paga) VALUES
  (1, 1, '2026-03-02', TRUE),
  (2, 1, '2026-03-02', FALSE),
  (3, 1, '2026-03-04', TRUE),
  (1, 2, '2026-03-05', TRUE),
  (4, 2, '2026-03-05', FALSE),
  (5, 2, '2026-03-06', TRUE),
  (6, 2, '2026-03-09', FALSE),
  (7, 2, '2026-03-09', TRUE),
  (2, 3, '2026-03-10', TRUE),
  (5, 3, '2026-03-10', FALSE),
  (8, 3, '2026-03-11', TRUE),
  (3, 3, '2026-03-12', TRUE);