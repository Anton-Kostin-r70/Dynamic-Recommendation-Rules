IF NOT EXISTS (
        SELECT FROM pg_database
        WHERE datname = 'recommendation'
    ) THEN
        CREATE DATABASE recommendation OWNER postgres;
        RAISE NOTICE 'Database "recommendation" created successfully.';
    ELSE
        RAISE NOTICE 'Database "recommendation" already exists.';
END IF;

-- Для проверки используется скрипт
-- Вставляем 5 пользователей
INSERT INTO users (id, name) VALUES
(1, 'Иван Иванов'),
(2, 'Пётр Петров'),
(3, 'Мария Сидорова'),
(4, 'Анна Козлова'),
(5, 'Дмитрий Смирнов');

-- Вставляем правила (rules) с корректными product_id и product_type
INSERT INTO rule (id, product_id, product_name, product_text, created_at) VALUES
('123e4567-e89b-12d3-a456-426614174000', '11111111-1111-1111-1111-111111111111', 'Дебетовый счёт', 'Рекомендация для дебетовых продуктов', NOW()),
('123e4567-e89b-12d3-a456-426614174001', '22222222-2222-2222-2222-222222222222', 'Кредитный продукт', 'Рекомендация для кредитных продуктов', NOW()),
('123e4567-e89b-12d3-a456-426614174002', '33333333-3333-3333-3333-333333333333', 'Инвестиционный продукт', 'Рекомендация для инвестиционных продуктов', NOW()),
('123e4567-e89b-12d3-a456-426614174003', '44444444-4444-4444-4444-444444444444', 'Сберегательный продукт', 'Рекомендация для сберегательных продуктов', NOW());

-- Вставляем запросы (queries) для правил
INSERT INTO query (id, rule_id, query_type, negate) VALUES
-- Для дебетового счёта
('223e4567-e89b-12d3-a456-426614175000', '123e4567-e89b-12d3-a456-426614174000', 'USER_OF', false),
-- Для кредитного продукта
('223e4567-e89b-12d3-a456-426614175001', '123e4567-e89b-12d3-a456-426614174001', 'ACTIVE_USER_OF', false),
-- Для инвестиционного продукта
('223e4567-e89b-12d3-a456-426614175002', '123e4567-e89b-12d3-a456-426614174002', 'TRANSACTION_SUM_COMPARE', false),
-- Для сберегательного продукта
('223e4567-e89b-12d3-a456-426614175003', '123e4567-e89b-12d3-a456-426614174003', 'TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW', false);

-- Вставляем аргументы для запросов с корректными значениями ProductType
INSERT INTO query_argument (query_id, argument) VALUES
-- Аргументы для USER_OF (дебетовый)
('223e4567-e89b-12d3-a456-426614175000', 'DEBIT'),
-- Аргументы для ACTIVE_USER_OF (кредитный)
('223e4567-e89b-12d3-a456-426614175001', 'CREDIT'),
-- Аргументы для TRANSACTION_SUM_COMPARE (инвестиционный)
('223e4567-e89b-12d3-a456-426614175002', 'INVEST'),
('223e4567-e89b-12d3-a456-426614175002', 'DEPOSIT'),
('223e4567-e89b-12d3-a456-426614175002', '>'),
('223e4567-e89b-12d3-a456-426614175002', '5000'),
-- Аргументы для TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW (сберегательный)
('223e4567-e89b-12d3-a456-426614175003', 'SAVING'),
('223e4567-e89b-12d3-a456-426614175003', '>');

-- Вставляем транзакции для пользователей (по 5 транзакций на каждого)

-- Пользователь 1: Иван Иванов (id=1)
INSERT INTO transactions (user_id, product_type, transaction_type, amount, created_at) VALUES
(1, 'DEBIT', 'DEPOSIT', 10000.00, NOW() - INTERVAL '1 day'),
(1, 'DEBIT', 'WITHDRAW', 2000.00, NOW() - INTERVAL '2 days'),
(1, 'CREDIT', 'DEPOSIT', 5000.00, NOW() - INTERVAL '3 days'),
(1, 'SAVING', 'WITHDRAW', 1500.00, NOW() - INTERVAL '4 days'),
(1, 'INVEST', 'DEPOSIT', 3000.00, NOW() - INTERVAL '5 days');

-- Пользователь 2: Пётр Петров (id=2)
INSERT INTO transactions (user_id, product_type, transaction_type, amount, created_at) VALUES
(2, 'DEBIT', 'DEPOSIT', 8000.00, NOW() - INTERVAL '1 day'),
(2, 'CREDIT', 'WITHDRAW', 1500.00, NOW() - INTERVAL '2 days'),
(2, 'SAVING', 'DEPOSIT', 6000.00, NOW() - INTERVAL '3 days'),
(2, 'INVEST', 'WITHDRAW', 2000.00, NOW() - INTERVAL '4 days'),
(2, 'DEBIT', 'WITHDRAW', 4000.00, NOW() - INTERVAL '5 days');

-- Пользователь 3: Мария Сидорова (id=3)
INSERT INTO transactions (user_id, product_type, transaction_type, amount, created_at) VALUES
(3, 'CREDIT', 'DEPOSIT', 12000.00, NOW() - INTERVAL '1 day'),
(3, 'SAVING', 'WITHDRAW', 3000.00, NOW() - INTERVAL '2 days'),
(3, 'INVEST', 'DEPOSIT', 7000.00, NOW() - INTERVAL '3 days'),
(3, 'DEBIT', 'WITHDRAW', 2500.00, NOW() - INTERVAL '4 days'),
(3, 'SAVING', 'DEPOSIT', 5000.00, NOW() - INTERVAL '5 days');
-- Пользователь 4: Анна Козлова (id=4)
INSERT INTO transactions (user_id, product_type, transaction_type, amount, created_at) VALUES
(4, 'DEBIT', 'DEPOSIT', 9000.00, NOW() - INTERVAL '1 day'),
(4, 'CREDIT', 'WITHDRAW', 1800.00, NOW() - INTERVAL '2 days'),
(4, 'SAVING', 'DEPOSIT', 5500.00, NOW() - INTERVAL '3 days'),
(4, 'INVEST', 'WITHDRAW', 1700.00, NOW() - INTERVAL '4 days'),
(4, 'DEBIT', 'WITHDRAW', 3500.00, NOW() - INTERVAL '5 days');

-- Пользователь 5: Дмитрий Смирнов (id=5)
INSERT INTO transactions (user_id, product_type, transaction_type, amount, created_at) VALUES
(5, 'CREDIT', 'DEPOSIT', 11000.00, NOW() - INTERVAL '1 day'),
(5, 'SAVING', 'WITHDRAW', 2200.00, NOW() - INTERVAL '2 days'),
(5, 'INVEST', 'DEPOSIT', 6500.00, NOW() - INTERVAL '3 days'),
(5, 'DEBIT', 'WITHDRAW', 1900.00, NOW() - INTERVAL '4 days'),
(5, 'SAVING', 'DEPOSIT', 4500.00, NOW() - INTERVAL '5 days');