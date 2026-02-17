-- Migración: Agregar tipo_evento_id a registros_rufe
-- Fecha: 2026-01-26
-- Descripción: Agrega referencia al catálogo de tipos de eventos

-- Agregar columna tipo_evento_id
ALTER TABLE registros_rufe 
ADD COLUMN tipo_evento_id INTEGER;

-- Agregar foreign key constraint
ALTER TABLE registros_rufe
ADD CONSTRAINT fk_registros_rufe_tipo_evento
FOREIGN KEY (tipo_evento_id) REFERENCES evento(id);

-- Crear índice para mejorar performance en consultas
CREATE INDEX idx_registros_rufe_tipo_evento 
ON registros_rufe(tipo_evento_id);

-- Comentario en la columna
COMMENT ON COLUMN registros_rufe.tipo_evento_id IS 'FK a tabla evento (catálogo de tipos de eventos: Inundación, Derrumbe, etc.)';
