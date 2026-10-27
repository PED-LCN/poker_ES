ALTER TABLE users
    RENAME COLUMN password_hash TO senha;

Alter Table users
    ADD COLUMN role VARCHAR(30) NOT NULL DEFAULT 'USER';
