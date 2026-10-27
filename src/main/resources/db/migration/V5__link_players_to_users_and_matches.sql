ALTER TABLE players ADD COLUMN user_id BIGINT REFERENCES users(id);
ALTER TABLE players ADD COLUMN match_id BIGINT REFERENCES matches(id);
ALTER TABLE players ADD CONSTRAINT uq_players_match_user UNIQUE (match_id, user_id);
-- Registros legados não têm como ser associados a uma conta automaticamente.
-- A restrição vale para novos jogadores sem apagar os registros anteriores.
ALTER TABLE players ADD CONSTRAINT ck_players_user_and_match
    CHECK (user_id IS NOT NULL AND match_id IS NOT NULL) NOT VALID;
