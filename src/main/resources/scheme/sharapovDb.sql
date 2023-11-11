-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS users_statistics;
DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS code;
DROP TABLE IF EXISTS language;
DROP TABLE IF EXISTS complexity;

-- Создаем таблицу "language"
CREATE TABLE language (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Создаем таблицу "users"
CREATE TABLE "users"(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255)
);

-- Создаем таблицу "complexity"
CREATE TABLE complexity (
    id SERIAL PRIMARY KEY,
    complexity VARCHAR(255) NOT NULL
);

-- Создаем таблицу "code"
CREATE TABLE code (
    id SERIAL PRIMARY KEY,
    language_id INT NOT NULL,
    complexity_id INT,
    code TEXT,
    FOREIGN KEY (language_id) REFERENCES language(id),
    FOREIGN KEY (complexity_id) REFERENCES complexity(id)
);

-- Создаем таблицу "users_statistics"
CREATE TABLE users_statistics (
    id SERIAL PRIMARY KEY,
    users_id INT NOT NULL,
    user_code TEXT,
    generated_code TEXT,
    lead_time FLOAT,
    count_words INT,
    match_percentage FLOAT,
    date_of_completion DATE,
    FOREIGN KEY (users_id) REFERENCES "users"(id)
);
