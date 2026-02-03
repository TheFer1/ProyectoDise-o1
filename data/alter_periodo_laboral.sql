-- Agregar campos de periodo laboral a la tabla formularios
-- Ejecutar estos comandos en la base de datos para actualizar la estructura

ALTER TABLE formularios ADD COLUMN periodo_laboral_desde TEXT;
ALTER TABLE formularios ADD COLUMN periodo_laboral_hasta TEXT;

-- Comentario: 
-- periodo_laboral_desde: Fecha de inicio del periodo laboral (formato: DD/MM/YYYY)
-- periodo_laboral_hasta: Fecha de fin del periodo laboral (formato: DD/MM/YYYY)
