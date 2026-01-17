-- database: data.sqlite
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    apellido TEXT NOT NULL,
    correo TEXT NOT NULL UNIQUE,
    contraseña TEXT NOT NULL,
    tipo TEXT NOT NULL DEFAULT 'Usuario'
);

CREATE TABLE proyectos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    codigo TEXT NOT NULL UNIQUE,
    fecha_inicio DATE,
    fecha_fin DATE,
    coi TEXT,
    num_ayudantes INTEGER DEFAULT 0,
    descripcion TEXT,
    tipo TEXT,
    id_usuario INTEGER,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE formularios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_ayudantes INTEGER,
    nombre_ayudante TEXT NOT NULL,
    apellido_ayudante TEXT NOT NULL,
    cedula TEXT NOT NULL,
    facultad TEXT,
    estado TEXT DEFAULT 'Pendiente',
    id_proyecto INTEGER DEFAULT 0,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id)
);

CREATE TABLE solicitudes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha DATE NOT NULL,
    asunto TEXT NOT NULL,
    estado TEXT DEFAULT 'Pendiente',
    id_usuario INTEGER,
    tipo TEXT,
    codigo_permiso TEXT,
    tipo_documento TEXT,
    id_director INTEGER,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_director) REFERENCES usuarios(id)
);

CREATE TABLE notificaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha DATE NOT NULL,
    informacion TEXT NOT NULL,
    id_usuario INTEGER NOT NULL,
    leida INTEGER DEFAULT 0,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE planificacion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_proyecto INTEGER,
    maximo_ayudantes INTEGER NOT NULL,
    ayudantes_registrados INTEGER DEFAULT 0,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id)
); 

ALTER TABLE proyectos ADD COLUMN descripcion TEXT;
ALTER TABLE proyectos ADD COLUMN tipo TEXT;

 ALTER TABLE proyectos ADD COLUMN id_usuario TEXT; 

 ALTER TABLE formularios ADD COLUMN id_proyecto INTEGER DEFAULT 0;

 ALTER TABLE solicitudes ADD COLUMN id_director INTEGER;