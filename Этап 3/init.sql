CREATE TABLE directions (
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE levels (
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TYPE pair_status AS ENUM ('active', 'paused', 'completed');

CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    last_name     TEXT NOT NULL,
    first_name    TEXT NOT NULL,
    middle_name   TEXT,
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL DEFAULT ''
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_name ON users(last_name, first_name);

CREATE TABLE user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role    TEXT NOT NULL CHECK (role IN ('mentor', 'mentee')),
    PRIMARY KEY (user_id, role)
);

CREATE TABLE mentors (
    user_id   INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE mentees (
    user_id   INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    level_id  INTEGER NOT NULL REFERENCES levels(id) ON DELETE RESTRICT
);

CREATE TABLE mentor_directions (
    mentor_user_id INTEGER NOT NULL REFERENCES mentors(user_id) ON DELETE CASCADE,
    direction_id   INTEGER NOT NULL REFERENCES directions(id) ON DELETE CASCADE,
    PRIMARY KEY (mentor_user_id, direction_id)
);

CREATE TABLE mentee_goals (
    id        SERIAL PRIMARY KEY,
    mentee_user_id INTEGER NOT NULL REFERENCES mentees(user_id) ON DELETE CASCADE,
    goal_text TEXT NOT NULL
);

CREATE INDEX idx_goals_mentee ON mentee_goals(mentee_user_id);

CREATE TABLE pairs (
    id          SERIAL PRIMARY KEY,
    mentor_user_id INTEGER NOT NULL REFERENCES mentors(user_id) ON DELETE RESTRICT,
    mentee_user_id INTEGER NOT NULL REFERENCES mentees(user_id) ON DELETE RESTRICT,
    start_date  DATE NOT NULL,
    status      pair_status NOT NULL DEFAULT 'active',
    UNIQUE (mentor_user_id, mentee_user_id)
);

CREATE INDEX idx_pairs_mentor ON pairs(mentor_user_id);
CREATE INDEX idx_pairs_mentee ON pairs(mentee_user_id);
CREATE INDEX idx_pairs_status ON pairs(status);

CREATE TABLE development_plans (
    id          SERIAL PRIMARY KEY,
    pair_id     INTEGER NOT NULL REFERENCES pairs(id) ON DELETE CASCADE,
    title       TEXT NOT NULL,
    description TEXT,
    deadline    DATE
);

CREATE INDEX idx_plans_pair ON development_plans(pair_id);

CREATE TABLE meetings (
    id            SERIAL PRIMARY KEY,
    pair_id       INTEGER NOT NULL REFERENCES pairs(id) ON DELETE CASCADE,
    datetime      TIMESTAMP NOT NULL,
    topic         TEXT NOT NULL,
    tasks_done    TEXT,
    mentor_rating INTEGER CHECK (mentor_rating BETWEEN 1 AND 5),
    mentee_rating INTEGER CHECK (mentee_rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_meetings_pair ON meetings(pair_id);
CREATE INDEX idx_meetings_datetime ON meetings(datetime);

INSERT INTO directions (name) VALUES
('IT'),
('Педагогика'),
('Инженерия');

INSERT INTO levels (name, description) VALUES
('Beginner', 'Только начинает'),
('Novice', 'Знает основы'),
('Intermediate', 'Может решать типовые задачи'),
('Advanced', 'Работает с фреймворками'),
('Expert', 'Готов к профессиональной работе');

INSERT INTO users (last_name, first_name, middle_name, email) VALUES
('Сидоров', 'Алексей', 'Петрович', 'sidorov@example.com'),
('Кузнецова', 'Мария', NULL, 'kuznetsova@example.com'),
('Иванов', 'Дмитрий', 'Сергеевич', 'ivanov.d@example.com'),
('Петрова', 'Анна', NULL, 'petrova.a@example.com');

INSERT INTO user_roles (user_id, role) VALUES
(1, 'mentor'),
(2, 'mentor'),
(3, 'mentee'),
(4, 'mentee');

INSERT INTO mentors (user_id, available) VALUES
(1, TRUE),
(2, FALSE);

INSERT INTO mentees (user_id, level_id) VALUES
(3, 2),
(4, 3);

INSERT INTO mentor_directions (mentor_user_id, direction_id) VALUES
(1, 1),
(1, 3),
(2, 2);

INSERT INTO mentee_goals (mentee_user_id, goal_text) VALUES
(3, 'Выучить основы Python'),
(3, 'Сделать первое резюме'),
(4, 'Подготовиться к стажировке в IT-компании');

INSERT INTO pairs (mentor_user_id, mentee_user_id, start_date, status) VALUES
(1, 3, '2025-09-01', 'active'),
(2, 4, '2025-09-10', 'paused');

INSERT INTO development_plans (pair_id, title, description, deadline) VALUES
(1, 'Основы программирования', 'Пройти Python, Git, алгоритмы', '2025-12-01'),
(2, 'Педагогическая практика', 'Подготовить уроки и провести мини-занятия', '2025-11-30');

INSERT INTO meetings (pair_id, datetime, topic, tasks_done, mentor_rating, mentee_rating) VALUES
(1, '2025-09-05 18:00:00', 'Введение в Python', 'Установили Python, написали "Hello World"', 4, 5),
(1, '2025-09-12 18:00:00', 'Переменные и типы', 'Разобрали int, str, bool; решили 3 задачи', 5, 4),
(2, '2025-09-15 17:00:00', 'Планирование урока', 'Составили план занятия для 5 класса', 3, 4);