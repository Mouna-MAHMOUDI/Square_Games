package com.mouna.square_games.Dao;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

//@Repository
public class JdbcGameDao implements GameDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final List<GameFactory> gameFactories;

    public JdbcGameDao(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            List<GameFactory> gameFactories) {

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.gameFactories = gameFactories;
    }

    // ============================================================
    // FIND ALL
    // ============================================================

    @Override
    public Stream<Game> findAll() {

        String sql = """
                SELECT id
                FROM games
                """;

        List<String> gameIds = namedParameterJdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getObject("id", UUID.class).toString()
        );

        return gameIds.stream()
                .map(this::findById)
                .flatMap(Optional::stream);
    }

    // ============================================================
    // FIND BY ID
    // ============================================================

    @Override
    public Optional<Game> findById(String gameId) {

        String sql = """
                SELECT
                    id,
                    factory_id,
                    status,
                    current_player_id,
                    board_size
                FROM games
                WHERE id = :gameId
                """;

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue("gameId", UUID.fromString(gameId));

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (rs, rowNum) -> {

                    UUID id =
                            rs.getObject("id", UUID.class);

                    String factoryId =
                            rs.getString("factory_id");

                    int boardSize =
                            rs.getInt("board_size");

                    // ------------------------------------------------
                    // Recherche de la GameFactory
                    // ------------------------------------------------

                    GameFactory factory =
                            gameFactories.stream()
                                    .filter(f ->
                                            f.getGameFactoryId()
                                                    .equals(factoryId))
                                    .findFirst()
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(
                                                    "Aucune GameFactory trouvée pour : "
                                                            + factoryId
                                            )
                                    );

                    // ------------------------------------------------
                    // Récupération des joueurs
                    // ------------------------------------------------

                    String playersSql = """
                            SELECT player_id
                            FROM game_players
                            WHERE game_id = :gameId
                            ORDER BY player_order
                            """;

                    List<UUID> players =
                            namedParameterJdbcTemplate.query(
                                    playersSql,
                                    parameters,
                                    (playerRs, playerRowNum) ->
                                            playerRs.getObject(
                                                    "player_id",
                                                    UUID.class
                                            )
                            );

                    // ------------------------------------------------
                    // Récupération des tokens présents sur le plateau
                    // ------------------------------------------------

                    String boardTokensSql = """
                            SELECT
                                owner_id,
                                name,
                                x,
                                y
                            FROM game_tokens
                            WHERE game_id = :gameId
                              AND token_status = 'BOARD'
                            """;

                    List<TokenPosition<UUID>> boardTokens =
                            namedParameterJdbcTemplate.query(
                                    boardTokensSql,
                                    parameters,
                                    (tokenRs, tokenRowNum) -> {

                                        UUID ownerId =
                                                tokenRs.getObject(
                                                        "owner_id",
                                                        UUID.class
                                                );

                                        String tokenName =
                                                tokenRs.getString("name");

                                        int x =
                                                tokenRs.getInt("x");

                                        int y =
                                                tokenRs.getInt("y");

                                        return new TokenPosition<>(
                                                ownerId,
                                                tokenName,
                                                x,
                                                y
                                        );
                                    }
                            );

                    // ------------------------------------------------
                    // Récupération des tokens retirés
                    // ------------------------------------------------

                    String removedTokensSql = """
                            SELECT
                                owner_id,
                                name
                            FROM game_tokens
                            WHERE game_id = :gameId
                              AND token_status = 'REMOVED'
                            """;

                    List<TokenPosition<UUID>> removedTokens =
                            namedParameterJdbcTemplate.query(
                                    removedTokensSql,
                                    parameters,
                                    (tokenRs, tokenRowNum) -> {

                                        UUID ownerId =
                                                tokenRs.getObject(
                                                        "owner_id",
                                                        UUID.class
                                                );

                                        String tokenName =
                                                tokenRs.getString("name");

                                        return new TokenPosition<>(
                                                ownerId,
                                                tokenName,
                                                0,
                                                0
                                        );
                                    }
                            );

                    // ------------------------------------------------
                    // Reconstruction de la partie
                    // ------------------------------------------------

                    try {

                        return factory.createGameWithIds(
                                id,
                                boardSize,
                                players,
                                boardTokens,
                                removedTokens
                        );

                    } catch (InconsistentGameDefinitionException e) {

                        throw new IllegalStateException(
                                "Impossible de reconstruire la partie "
                                        + id,
                                e
                        );
                    }
                }
        ).stream().findFirst();
    }

    // ============================================================
    // UPSERT
    // ============================================================

    @Override
    public Game upsert(Game game) {

        // ------------------------------------------------------------
        // 1. Enregistrement de la partie
        // ------------------------------------------------------------

        String sql = """
                INSERT INTO games (
                    id,
                    factory_id,
                    status,
                    current_player_id,
                    board_size
                )
                VALUES (
                    :id,
                    :factoryId,
                    :status,
                    :currentPlayerId,
                    :boardSize
                )
                ON CONFLICT (id)
                DO UPDATE SET
                    factory_id = :factoryId,
                    status = :status,
                    current_player_id = :currentPlayerId,
                    board_size = :boardSize
                """;

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue("id", game.getId())
                        .addValue("factoryId", game.getFactoryId())
                        .addValue("status", game.getStatus().name())
                        .addValue(
                                "currentPlayerId",
                                game.getCurrentPlayerId()
                        )
                        .addValue("boardSize", game.getBoardSize());

        namedParameterJdbcTemplate.update(
                sql,
                parameters
        );

        // ------------------------------------------------------------
        // 2. Suppression des anciens joueurs
        // ------------------------------------------------------------

        String deletePlayersSql = """
                DELETE FROM game_players
                WHERE game_id = :gameId
                """;

        namedParameterJdbcTemplate.update(
                deletePlayersSql,
                new MapSqlParameterSource()
                        .addValue("gameId", game.getId())
        );

        // ------------------------------------------------------------
        // 3. Enregistrement des joueurs
        // ------------------------------------------------------------

        String insertPlayerSql = """
                INSERT INTO game_players (
                    game_id,
                    player_id,
                    player_order
                )
                VALUES (
                    :gameId,
                    :playerId,
                    :playerOrder
                )
                """;

        int playerOrder = 0;

        for (UUID playerId : game.getPlayerIds()) {

            MapSqlParameterSource playerParameters =
                    new MapSqlParameterSource()
                            .addValue("gameId", game.getId())
                            .addValue("playerId", playerId)
                            .addValue(
                                    "playerOrder",
                                    playerOrder
                            );

            namedParameterJdbcTemplate.update(
                    insertPlayerSql,
                    playerParameters
            );

            playerOrder++;
        }

        // ------------------------------------------------------------
        // 4. Suppression des anciens tokens
        // ------------------------------------------------------------

        String deleteTokensSql = """
                DELETE FROM game_tokens
                WHERE game_id = :gameId
                """;

        namedParameterJdbcTemplate.update(
                deleteTokensSql,
                new MapSqlParameterSource()
                        .addValue("gameId", game.getId())
        );

        // ------------------------------------------------------------
        // 5. Enregistrement des tokens
        // ------------------------------------------------------------

        String insertTokenSql = """
                INSERT INTO game_tokens (
                    game_id,
                    owner_id,
                    name,
                    token_status,
                    x,
                    y
                )
                VALUES (
                    :gameId,
                    :ownerId,
                    :name,
                    :tokenStatus,
                    :x,
                    :y
                )
                """;

        // ------------------------------------------------------------
        // Tokens présents sur le plateau
        // ------------------------------------------------------------

        game.getBoard().forEach((position, token) -> {

            UUID ownerId =
                    token.getOwnerId().orElse(null);

            MapSqlParameterSource tokenParameters =
                    new MapSqlParameterSource()
                            .addValue("gameId", game.getId())
                            .addValue("ownerId", ownerId)
                            .addValue("name", token.getName())
                            .addValue("tokenStatus", "BOARD")
                            .addValue("x", position.x())
                            .addValue("y", position.y());

            namedParameterJdbcTemplate.update(
                    insertTokenSql,
                    tokenParameters
            );
        });

        // ------------------------------------------------------------
        // Tokens retirés du plateau
        // ------------------------------------------------------------

        for (var token : game.getRemovedTokens()) {

            UUID ownerId =
                    token.getOwnerId().orElse(null);

            MapSqlParameterSource tokenParameters =
                    new MapSqlParameterSource()
                            .addValue("gameId", game.getId())
                            .addValue("ownerId", ownerId)
                            .addValue("name", token.getName())
                            .addValue("tokenStatus", "REMOVED")
                            .addValue("x", null)
                            .addValue("y", null);

            namedParameterJdbcTemplate.update(
                    insertTokenSql,
                    tokenParameters
            );
        }

        // ------------------------------------------------------------
        // 6. Retourne la partie
        // ------------------------------------------------------------

        return game;
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void delete(String gameId) {

        String sql = """
                DELETE FROM games
                WHERE id = :gameId
                """;

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue(
                                "gameId",
                                UUID.fromString(gameId)
                        );

        namedParameterJdbcTemplate.update(
                sql,
                parameters
        );
    }
}
