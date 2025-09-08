-- =========================================
-- Script de base de datos - Microservicio Solicitudes de Préstamo
-- Sistema: CrediYa
-- =========================================

-- ============================
-- Crear base de datos si no existe
-- ============================
CREATE
    DATABASE IF NOT EXISTS crediya_solicitudes_db;
USE
    crediya_solicitudes_db;


-- ============================
-- Tabla de Estados
-- ============================
CREATE TABLE estados
(
    id_estado   INT PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

-- ============================
-- Datos iniciales para estados
-- ============================
INSERT INTO estados (nombre, descripcion)
VALUES ('PENDIENTE', 'La solicitud fue registrada y está en proceso de revisión'),
       ('APROBADA', 'La solicitud fue aprobada exitosamente'),
       ('RECHAZADA', 'La solicitud fue rechazada');


-- ============================
-- Tabla de Tipos de Préstamo
-- ============================
CREATE TABLE tipo_prestamo
(
    id_tipo_prestamo      INT PRIMARY KEY AUTO_INCREMENT,
    nombre                VARCHAR(100)   NOT NULL,
    monto_minimo          DECIMAL(15, 2) NOT NULL,
    monto_maximo          DECIMAL(15, 2) NOT NULL,
    tasa_interes          DECIMAL(5, 2)  NOT NULL,
    validacion_automatica BOOLEAN        NOT NULL DEFAULT FALSE
);

-- ============================
-- Datos iniciales para tipos de préstamo
-- ============================
INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
VALUES ('PERSONAL', 500000, 20000000, 18.50, TRUE),
       ('HIPOTECARIO', 20000000, 500000000, 10.25, FALSE),
       ('VEHICULAR', 10000000, 200000000, 12.75, FALSE),
       ('LIBRE INVERSION', 1000000, 50000000, 20.00, TRUE);


-- ============================
-- Tabla de Solicitudes
-- ============================
CREATE TABLE solicitud
(
    id_solicitud        INT PRIMARY KEY AUTO_INCREMENT,
    documento_identidad INT            NOT NULL,
    monto               DECIMAL(15, 2) NOT NULL,
    plazo               INT            NOT NULL,
    email               VARCHAR(150)   NULL,
    id_estado           INT            NOT NULL,
    id_tipo_prestamo    INT            NOT NULL,
    FOREIGN KEY (id_estado) REFERENCES estados (id_estado),
    FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo (id_tipo_prestamo)
);
