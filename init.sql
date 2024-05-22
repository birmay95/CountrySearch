-- Создание таблицы country
CREATE TABLE country (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         capital VARCHAR(255),
                         population DOUBLE PRECISION,
                         area DOUBLE PRECISION,
                         gdp DOUBLE PRECISION
);

-- Создание таблицы city
CREATE TABLE city (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      population DOUBLE PRECISION,
                      area DOUBLE PRECISION,
                      country_id INTEGER,
                      FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE CASCADE
);

-- Создание таблицы nation
CREATE TABLE nation (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        language VARCHAR(255),
                        religion VARCHAR(255)
);

-- Создание таблицы country_nations для связи многие ко многим между country и nation
CREATE TABLE country_nations (
                                 country_id INTEGER,
                                 nation_id INTEGER,
                                 PRIMARY KEY (country_id, nation_id),
                                 FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE CASCADE,
                                 FOREIGN KEY (nation_id) REFERENCES nation(id) ON DELETE CASCADE
);

-- Вставка начальных данных в таблицу country
INSERT INTO country (name, capital, population, area, gdp) VALUES
    ('Belarus', 'Minsk', 9.5E6, 2.07E5, 6.5E10);

-- Вставка начальных данных в таблицу city
INSERT INTO city (name, population, area, country_id) VALUES
                                                          ('Minsk', 2E6, 348.84, 1),
                                                          ('Gomel', 0.5E6, 121.0, 1);

-- Вставка начальных данных в таблицу nation
INSERT INTO nation (name, language, religion) VALUES
                                                  ('Belarusian', 'Belarusian', 'Christian'),
                                                  ('Russian', 'Russian', 'Christian');

-- Вставка начальных данных в таблицу country_nations
INSERT INTO country_nations (country_id, nation_id) VALUES
                                                        (1, 1),
                                                        (1, 2);
