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
    cuota_mensual DECIMAL(9,2) NOT NULL,
    CONSTRAINT pk_id_curso PRIMARY KEY (id_curso),
    CONSTRAINT uq_curso_codigo UNIQUE (codigo)
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
    fecha DATE DEFAULT(CURRENT_DATE()),
    cuota VARCHAR(6) NOT NULL,
    CONSTRAINT pk_inscripcion PRIMARY KEY (id_alumno, id_curso),
    CONSTRAINT chk_inscripcion_cuota CHECK (cuota IN('Pagada', 'Adeuda')),
    CONSTRAINT fk_inscripcion_alumno FOREIGN KEY id_alumno REFERENCES alumno(id_alumno),
    CONSTRAINT fk_inscripcion_curso FOREIGN KEY id_curso REFERENCES curso(id_curso)
);