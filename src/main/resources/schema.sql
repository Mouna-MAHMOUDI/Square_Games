CREATE TABLE IF NOT EXISTS games (
                       id UUID PRIMARY KEY,
                       factory_id VARCHAR(100) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       current_player_id UUID,
                       board_size INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS game_players (
                              game_id UUID NOT NULL,
                              player_id UUID NOT NULL,
                              player_order INTEGER NOT NULL,

                              PRIMARY KEY (game_id, player_id),

                              FOREIGN KEY (game_id)
                                  REFERENCES games(id)
                                  ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS game_tokens (
                             id BIGSERIAL PRIMARY KEY,
                             game_id UUID NOT NULL,
                             owner_id UUID,
                             name VARCHAR(100) NOT NULL,
                             token_status VARCHAR(20) NOT NULL,
                             x INTEGER,
                             y INTEGER,

                             FOREIGN KEY (game_id)
                                 REFERENCES games(id)
                                 ON DELETE CASCADE
);