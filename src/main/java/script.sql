IF NOT EXISTS (
        SELECT FROM pg_database
        WHERE datname = 'recommendation'
    ) THEN
        CREATE DATABASE recommendation OWNER postgres;
        RAISE NOTICE 'Database "recommendation" created successfully.';
    ELSE
        RAISE NOTICE 'Database "recommendation" already exists.';
END IF;