-- ExpresoFast - Laboratorio 11 (SQL Server)
-- Ejecutar con: sqlcmd -S localhost -E -C -i lab11-sqlserver.sql
IF DB_ID('ExpresoFastLab11_C5J263') IS NULL
    CREATE DATABASE ExpresoFastLab11_C5J263 COLLATE Latin1_General_CI_AS;
GO
USE ExpresoFastLab11_C5J263;
GO

IF OBJECT_ID('PAQUETES') IS NOT NULL DROP TABLE PAQUETES;
IF OBJECT_ID('ENVIOS') IS NOT NULL DROP TABLE ENVIOS;
GO

CREATE TABLE ENVIOS (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo_rastreo VARCHAR(50) NOT NULL UNIQUE,
    destinatario VARCHAR(255) NOT NULL,
    direccion_destino VARCHAR(255) NOT NULL,
    monto_flete FLOAT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_creacion DATETIME2 NOT NULL,
    fecha_despacho DATE NULL,
    fecha_entrega_estimada DATE NULL
);
GO

-- Script oficial del laboratorio (tabla PAQUETES, relacion 1:N con ENVIOS)
CREATE TABLE PAQUETES (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    envio_id BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    peso_kg DECIMAL(5,2) NOT NULL,
    CONSTRAINT FK_Paquetes_Envios FOREIGN KEY (envio_id) REFERENCES ENVIOS(id) ON DELETE CASCADE
);
GO

-- Datos semilla
INSERT INTO ENVIOS (codigo_rastreo, destinatario, direccion_destino, monto_flete, estado, fecha_creacion, fecha_despacho, fecha_entrega_estimada) VALUES
('EXP-2026-1001', 'Juan Perez',    'San Jose Centro', 2500, 'PENDIENTE',   SYSDATETIME(), '2026-10-01', '2026-10-05'),
('EXP-2026-1002', 'Maria Lopez',   'Alajuela, Parque', 4500, 'EN_TRANSITO', SYSDATETIME(), '2026-10-01', '2026-10-06'),
('EXP-2026-1003', 'Carlos Campos', 'Cartago, TEC',     3000, 'ENTREGADO',   SYSDATETIME(), '2026-09-28', '2026-10-02'),
('EXP-2026-1004', 'Ana Jimenez',   'Heredia, Mall',    5000, 'CANCELADO',   SYSDATETIME(), '2026-09-29', '2026-10-03');
INSERT INTO PAQUETES (envio_id, descripcion, peso_kg) VALUES
(1, 'Caja de documentos', 1.50),
(2, 'Repuestos electronicos', 4.20),
(2, 'Cable y accesorios', 0.80);
GO


-- Usuario de aplicacion usado por Spring Boot
USE master;
IF SUSER_ID('lab11_user') IS NULL CREATE LOGIN lab11_user WITH PASSWORD='Lab11_Expreso#2026', CHECK_POLICY=OFF;
GO
USE ExpresoFastLab11_C5J263;
IF USER_ID('lab11_user') IS NULL CREATE USER lab11_user FOR LOGIN lab11_user;
ALTER ROLE db_owner ADD MEMBER lab11_user;
GO
