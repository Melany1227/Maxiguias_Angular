-- Script para agregar el campo CORREO a la tabla USUARIOS

-- 1. Verificar usuarios duplicados antes de agregar el índice único
SELECT USUARIO, COUNT(*) as cantidad 
FROM USUARIOS 
WHERE USUARIO IS NOT NULL 
GROUP BY USUARIO 
HAVING COUNT(*) > 1;

-- 2. Verificar correos duplicados si ya existen datos
SELECT CORREO, COUNT(*) as cantidad 
FROM USUARIOS 
WHERE CORREO IS NOT NULL AND CORREO != ''
GROUP BY CORREO 
HAVING COUNT(*) > 1;

-- 3. Agregar columna CORREO a tabla USUARIOS (solo si no existe)
ALTER TABLE USUARIOS ADD COLUMN IF NOT EXISTS CORREO VARCHAR(100) NULL;

-- 4. Crear índice único para el correo (solo después de verificar que no hay duplicados)
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_correo ON USUARIOS(CORREO) WHERE CORREO IS NOT NULL;

-- Verificar usuarios con valores nulos o vacíos
SELECT COUNT(*) as usuarios_nulos_o_vacios
FROM USUARIOS 
WHERE USUARIO IS NULL OR USUARIO = '';