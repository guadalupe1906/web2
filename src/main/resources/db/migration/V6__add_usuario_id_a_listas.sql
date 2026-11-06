-- Agregamos la columna usuario_id a la tabla listas permitiendo nulos inicialmente (o foreign key directa si está vacía)
ALTER TABLE listas ADD COLUMN usuario_id BIGINT;

-- Agregamos la foreign key hacia la tabla usuarios
ALTER TABLE listas 
ADD CONSTRAINT fk_listas_usuario 
FOREIGN KEY (usuario_id) REFERENCES usuarios(id);