CREATE TABLE directions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE mentors (
    id SERIAL PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    email VARCHAR(255) NOT NULL UNIQUE,
    specialization TEXT,
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE mentor_directions (
    mentor_id INTEGER NOT NULL REFERENCES mentors(id) ON DELETE CASCADE,
    direction_id INTEGER NOT NULL REFERENCES directions(id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_id, direction_id)
);

CREATE TABLE mentees (
    id SERIAL PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    email VARCHAR(255) NOT NULL UNIQUE,
    goals TEXT,
    current_level SMALLINT CHECK (current_level BETWEEN 1 AND 5)
);

CREATE TABLE pairs (
    id SERIAL PRIMARY KEY,
    mentor_id INTEGER NOT NULL REFERENCES mentors(id) ON DELETE RESTRICT,
    mentee_id INTEGER NOT NULL REFERENCES mentees(id) ON DELETE RESTRICT,
    start_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('active', 'paused', 'completed')),
    UNIQUE (mentor_id, mentee_id)
);

CREATE TABLE development_plans (
    id SERIAL PRIMARY KEY,
    pair_id INTEGER NOT NULL REFERENCES pairs(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deadline DATE
);

CREATE TABLE meetings (
    id SERIAL PRIMARY KEY,
    pair_id INTEGER NOT NULL REFERENCES pairs(id) ON DELETE CASCADE,
    datetime TIMESTAMP NOT NULL,
    topic VARCHAR(255),
    tasks_done TEXT,
    mentor_rating SMALLINT CHECK (mentor_rating BETWEEN 1 AND 5),
    mentee_rating SMALLINT CHECK (mentee_rating BETWEEN 1 AND 5)
);

INSERT INTO directions (name) VALUES
('IT'),
('Педагогика'),
('Инженерия'),
('Дизайн');

INSERT INTO mentors (last_name, first_name, middle_name, email, specialization, available) VALUES
('Иванов', 'Алексей', 'Сергеевич', 'a.ivanov@example.com', 'Full-stack разработка, Python, Django', TRUE),
('Петрова', 'Елена', 'Владимировна', 'e.petrova@example.com', 'UX/UI дизайн, Figma, прототипирование', TRUE),
('Сидоров', 'Дмитрий', NULL, 'd.sidorov@example.com', 'DevOps, Kubernetes, CI/CD', FALSE),
('Кузнецова', 'Мария', 'Андреевна', 'm.kuznetsova@example.com', 'Преподавание математики, методика обучения', TRUE);

INSERT INTO mentees (last_name, first_name, middle_name, email, goals, current_level) VALUES
('Смирнов', 'Артём', 'Олегович', 'a.smirnov@student.com', 'Освоить backend-разработку на Python и устроиться junior-разработчиком', 2),
('Волкова', 'Анастасия', NULL, 'n.volkova@student.com', 'Научиться проектировать пользовательские интерфейсы и создавать портфолио', 1),
('Морозов', 'Илья', 'Сергеевич', 'i.morozov@student.com', 'Разобраться с Kubernetes и автоматизацией развёртывания', 3),
('Лебедева', 'Полина', 'Дмитриевна', 'p.lebedeva@student.com', 'Подготовиться к карьере учителя математики в школе', 2);

INSERT INTO mentor_directions (mentor_id, direction_id) VALUES
(1, 1),
(2, 4),
(3, 1),
(4, 2);

INSERT INTO pairs (mentor_id, mentee_id, start_date, status) VALUES
(1, 1, '2025-10-01', 'active'),
(2, 2, '2025-10-05', 'active'),
(4, 4, '2025-09-15', 'completed'),
(3, 3, '2025-11-01', 'paused');

INSERT INTO development_plans (pair_id, title, description, deadline) VALUES
(1, 'Освоение Django', 'Пройти курс, сделать 2 проекта, подготовить резюме', '2026-03-01'),
(2, 'Создание портфолио', 'Сделать 3 кейса в Figma, получить фидбэк от ментора', '2026-01-15'),
(3, 'Педагогическая практика', 'Провести 10 уроков, подготовить учебные материалы', '2025-12-01'),
(4, 'Изучение CI/CD', 'Настроить пайплайн в GitLab CI для тестового проекта', '2026-02-28');

INSERT INTO meetings (pair_id, datetime, topic, tasks_done, mentor_rating, mentee_rating) VALUES
(1, '2025-10-10 18:00:00', 'Обзор целей и настройка окружения', 'Установлен Python, создана виртуальная среда', 5, 4),
(1, '2025-10-24 18:00:00', 'Первый Django-проект', 'Создан шаблонный сайт с базовой маршрутизацией', 4, 5),
(2, '2025-10-12 17:00:00', 'Основы компонентного дизайна', 'Создан первый компонент кнопки в Figma', 5, 5),
(3, '2025-11-20 16:00:00', 'Разбор проведённых уроков', 'Проведено 8 уроков, получены отзывы от коллег', 4, 4);