CREATE TABLE poker_rounds (
                              id BIGSERIAL PRIMARY KEY,
                              match_id BIGINT NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
                              status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_DISTRIBUICAO',
                              cards_dealt BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE round_participants (
                                    id BIGSERIAL PRIMARY KEY,
                                    round_id BIGINT NOT NULL REFERENCES poker_rounds(id) ON DELETE CASCADE,
                                    player_id BIGINT NOT NULL REFERENCES players(id),
                                    seat_order INT NOT NULL,
                                    eligible BOOLEAN NOT NULL DEFAULT TRUE,
                                    UNIQUE (round_id, player_id),
                                    UNIQUE (round_id, seat_order)
);

CREATE TABLE dealt_cards (
                             id BIGSERIAL PRIMARY KEY,
                             round_id BIGINT NOT NULL REFERENCES poker_rounds(id) ON DELETE CASCADE,
                             player_id BIGINT NOT NULL REFERENCES players(id),
                             suit VARCHAR(15) NOT NULL,
                             rank VARCHAR(15) NOT NULL,
                             deal_order INT NOT NULL,
                             UNIQUE (round_id, suit, rank),
                             UNIQUE (round_id, deal_order)
);

CREATE TABLE remaining_deck_cards (
                                      id BIGSERIAL PRIMARY KEY,
                                      round_id BIGINT NOT NULL REFERENCES poker_rounds(id) ON DELETE CASCADE,
                                      suit VARCHAR(15) NOT NULL,
                                      rank VARCHAR(15) NOT NULL,
                                      deck_position INT NOT NULL,
                                      UNIQUE (round_id, suit, rank),
                                      UNIQUE (round_id, deck_position)
);