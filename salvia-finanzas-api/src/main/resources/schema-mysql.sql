-- ===================================================================
-- SCRIPT DDL MYSQL - SALVIA FINANZAS (APF2 - UTP)
-- Estructura de tablas y relaciones para persistencia en MySQL
-- ===================================================================

CREATE DATABASE IF NOT EXISTS salviafinanzas_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE salviafinanzas_db;

-- 1. Tabla de Roles
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    points INT NOT NULL DEFAULT 0,
    streak INT NOT NULL DEFAULT 0,
    monthly_income DECIMAL(12, 2) DEFAULT 1500.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Tabla Intermedia Usuario - Roles (Muchos a Muchos)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Tabla de Categorías (Muchos a Uno con Usuario)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    monthly_budget DECIMAL(12, 2) DEFAULT 0.00,
    icon VARCHAR(50) DEFAULT 'category_default',
    color VARCHAR(20) DEFAULT '#4CAF50',
    user_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tabla de Gastos (Muchos a Uno con Usuario y con Categoría)
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(12, 2) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    description VARCHAR(255),
    expense_date DATETIME NOT NULL,
    recurrence VARCHAR(50) DEFAULT 'ONCE',
    category_id BIGINT,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expenses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_expenses_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 6. Tabla de Misiones de Ahorro (Muchos a Uno con Usuario)
CREATE TABLE IF NOT EXISTS savings_missions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    target_amount DECIMAL(12, 2) NOT NULL,
    current_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    deadline DATE,
    icon VARCHAR(100) DEFAULT 'savings_default',
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_savings_missions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;
