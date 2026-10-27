CREATE TABLE IF NOT EXISTS players (
                                       id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS poker_tables (
                                            id BIGSERIAL PRIMARY KEY,
                                            max_players INT NOT NULL DEFAULT 9,
                                            min_players_to_start INT NOT NULL DEFAULT 2
);

CREATE TABLE IF NOT EXISTS matches (
                                       id BIGSERIAL PRIMARY KEY,
                                       state VARCHAR(30),
                                       table_id BIGINT UNIQUE REFERENCES poker_tables(id),
                                       creator_id BIGINT REFERENCES players(id)
);

CREATE TABLE IF NOT EXISTS table_players (
                                             table_id BIGINT NOT NULL REFERENCES poker_tables(id) ON DELETE CASCADE,
                                             player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE
);

