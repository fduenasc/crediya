-- =========================================
-- Script de base de datos - Microservicio Autenticación
-- Sistema: CrediYa
-- =========================================

-- ============================
-- Crear base de datos si no existe
-- ============================
CREATE
    DATABASE IF NOT EXISTS crediya_autenticacion_db;
USE
    crediya_autenticacion_db;


-- ============================
-- Tabla de Rol
-- ============================
CREATE TABLE role
(
    uniqueID    INT PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- ============================
-- Datos iniciales para roles
-- ============================
INSERT INTO role (nombre, descripcion)
VALUES ('ADMIN', 'Usuario con permisos completos para gestionar préstamos, usuarios, reportes y configuraciones.'),
       ('CLIENT', 'Usuario que puede registrar solicitudes de préstamo y consultar el estado.'),
       ('ADVISOR', 'Usuario que revisa las solicitudes pre-aprobadas y toma decisiones finales.');



-- ============================
-- Tabla de User
-- ============================
CREATE TABLE user
(
    id_usuario          INT PRIMARY KEY AUTO_INCREMENT,
    nombre              VARCHAR(100) NOT NULL,
    apellido            VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    clave               VARCHAR(255) NOT NULL,
    documento_identidad VARCHAR(50),
    fecha_nacimiento    DATE,
    direccion           VARCHAR(255),
    telefono            VARCHAR(50),
    id_rol              INT          NOT NULL,
    salario_base        DECIMAL(15, 2) DEFAULT 0,
    FOREIGN KEY (id_rol) REFERENCES role (UniqueID)
);

-- ============================
-- Datos iniciales para usuarios
-- ============================
INSERT INTO user (nombre, apellido, email, documento_identidad, telefono, id_rol, salario_base, clave,
                  fecha_nacimiento, direccion)
VALUES ('Admin', 'CrediYa', 'admin@crediya.com', '123456789', '3001234567', 1, 0,
        '$2a$10$LM/iaZPnM.oALUVAYF3dyOdiIFoHHDKXz8ugasbwB9XyjwsJDfq4u',
        '1990-01-01', 'Bogotá, Colombia');

INSERT INTO user (nombre, apellido, email, documento_identidad, telefono, id_rol, salario_base, clave,
                  fecha_nacimiento, direccion)
VALUES ('Test', 'User', 'test@crediya.com', '987654321', '3009876543', 2, 2500000,
        '$2a$10$Nn6EMDMzEx/Et7ETuH4AZ.pgemDIl9H5znx4h3zs/ar.DrHOIZmQu',
        '1995-05-15', 'Medellín, Colombia');