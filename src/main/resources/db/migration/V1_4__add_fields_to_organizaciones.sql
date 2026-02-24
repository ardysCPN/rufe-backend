-- Migration to add missing fields to organizaciones table
ALTER TABLE public.organizaciones ADD COLUMN IF NOT EXISTS nit VARCHAR(50);
ALTER TABLE public.organizaciones ADD COLUMN IF NOT EXISTS direccion VARCHAR(255);
ALTER TABLE public.organizaciones ADD COLUMN IF NOT EXISTS telefono VARCHAR(50);
