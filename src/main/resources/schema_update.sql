-- 1. Tabla roles (Actualización o validación si ya existe)
CREATE TABLE IF NOT EXISTS roles (
  id bigserial PRIMARY KEY,
  nombre varchar(100) UNIQUE NOT NULL,
  fecha_creacion timestamp DEFAULT now()
);

-- 2. Tabla menu (Nueva estructura)
CREATE TABLE IF NOT EXISTS menu (
  id bigserial PRIMARY KEY,
  id_menu int4, -- Parent ID
  id_tipo_menu int4 NOT NULL, -- 1: Nivel 1, 2: Nivel 2, etc.
  router_url varchar(255),
  nombre_opcion varchar(150) NOT NULL,
  icono varchar(100),
  orden int4,
  fecha_creacion timestamp DEFAULT now(),
  FOREIGN KEY (id_menu) REFERENCES menu(id)
);

-- 3. Tabla menu_roles (Relación)
CREATE TABLE IF NOT EXISTS menu_roles (
  id bigserial PRIMARY KEY,
  menu_id int8 NOT NULL,
  rol_id int8 NOT NULL,
  FOREIGN KEY (menu_id) REFERENCES menu(id),
  FOREIGN KEY (rol_id) REFERENCES roles(id)
);
