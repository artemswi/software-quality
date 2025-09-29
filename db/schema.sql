-- Створення таблиці "власниктовара"
CREATE TABLE власниктовара (
    id INT PRIMARY KEY AUTO_INCREMENT,
    РНОКПП VARCHAR(11) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255) NOT NULL
);

-- Створення таблиці "декларація"
CREATE TABLE декларація (
    id INT PRIMARY KEY AUTO_INCREMENT,
    operation_type VARCHAR(50) NOT NULL,
    customs_value DECIMAL(10, 2) NOT NULL,
    export_country VARCHAR(50) NOT NULL,
    destination_country VARCHAR(50) NOT NULL,
    submission_date DATE NOT NULL,
    transport_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'В очікуванні',
    idВласникТовара INT NOT NULL,
    idПрацівник INT NOT NULL,
    FOREIGN KEY (idВласникТовара) REFERENCES власниктовара(id)
);

-- Створення таблиці "оглядтовара"
CREATE TABLE оглядтовара (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idПрацівника INT NOT NULL,
    статус_огляду VARCHAR(50) NOT NULL,
    idДекларації INT NOT NULL,
    дата DATE NOT NULL,
    FOREIGN KEY (idПрацівника) REFERENCES працівник(id),
    FOREIGN KEY (idДекларації) REFERENCES декларація(id)
);

-- Створення таблиці "працівник"
CREATE TABLE працівник (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    sex ENUM('чоловік', 'жінка') NOT NULL,
    contact_info VARCHAR(255) NOT NULL,
    rank VARCHAR(50) NOT NULL
);

-- Створення таблиці "тариф_країна"
CREATE TABLE тариф_країна (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_country VARCHAR(50) NOT NULL UNIQUE,
    rate DECIMAL(5, 2) NOT NULL
);

-- Створення таблиці "тариф_категорія"
CREATE TABLE тариф_категорія (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_category VARCHAR(50) NOT NULL UNIQUE,
    rate DECIMAL(5, 2) NOT NULL
);

-- Створення таблиці "тариф_напрямок"
CREATE TABLE тариф_напрямок (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL UNIQUE,
    rate DECIMAL(5, 2) NOT NULL
);

-- Створення таблиці "користувач"
CREATE TABLE користувач (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'broker', 'inspector') NOT NULL,
    idПрацівник INT NOT NULL,
    FOREIGN KEY (idПрацівник) REFERENCES працівник(id)
);

-- Вставка тестових даних для "власниктовара" (опціонально)
INSERT INTO власниктовара (РНОКПП, full_name, contact_info)
VALUES 
('12345678901', 'Іван Петров', '+380501234567'),
('98765432109', 'Олена Коваль', '+380671234567');

-- Вставка тестових даних для "працівник" (опціонально)
INSERT INTO працівник (full_name, birthday, sex, contact_info, rank)
VALUES 
('Микола Сидоренко', '1985-05-20', 'чоловік', '+380931234500', 'Інспектор митниці'),
('Анна Зозуля', '1990-02-15', 'жінка', '+380501234456', 'Брокер');

-- Вставка тестових даних для "користувач" (опціонально)
INSERT INTO користувач (username, password, role, idПрацівник)
VALUES 
('admin', 'password123', 'admin', 1),
('broker1', '1234', 'broker', 2);