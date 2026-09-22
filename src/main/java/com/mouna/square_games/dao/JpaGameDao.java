package com.mouna.square_games.dao;

import com.mouna.square_games.entity.GameEntityRepository;
import com.mouna.square_games.entity.GameEntity;
import com.mouna.square_games.entity.GameTokenEntity;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class JpaGameDao implements GameDao {

    private final GameEntityRepository gameRepository;
    private final List<GameFactory> gameFactories;

    public JpaGameDao(
            GameEntityRepository gameRepository,
            List<GameFactory> gameFactories) {

        this.gameRepository = gameRepository;
        this.gameFactories = gameFactories;
    }

    @Override
    public Stream<Game> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(this::toGame);
    }

    @Override
    public Optional<Game> findById(String gameId) {
        return gameRepository.findById(gameId)
                .map(this::toGame);
    }

    @Override
    public Game upsert(Game game) {

        GameEntity entity = toEntity(game);

        GameEntity savedEntity = gameRepository.save(entity);

        return toGame(savedEntity);
    }

    @Override
    public void delete(String gameId) {
        gameRepository.deleteById(gameId);
    }

    /**
     * Conversion Game → GameEntity
     */
    private GameEntity toEntity(Game game) {

        GameEntity entity = new GameEntity();

        entity.id = game.getId().toString();
        entity.factoryId = game.getFactoryId();
        entity.boardSize = game.getBoardSize();

        entity.playerIds = game.getPlayerIds()
                .stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));

        entity.tokens = new ArrayList<>();

        /*
         * Tokens actuellement présents sur le plateau.
         */
        game.getBoard().forEach((position, token) -> {

            GameTokenEntity tokenEntity = new GameTokenEntity();

            tokenEntity.ownerId = token.getOwnerId()
                    .map(UUID::toString)
                    .orElse(null);

            tokenEntity.name = token.getName();

            tokenEntity.removed = false;

            tokenEntity.x = position.x();
            tokenEntity.y = position.y();

            entity.tokens.add(tokenEntity);
        });

        /*
         * Tokens retirés.
         */
        game.getRemovedTokens().forEach(token -> {

            GameTokenEntity tokenEntity = new GameTokenEntity();

            tokenEntity.ownerId = token.getOwnerId()
                    .map(UUID::toString)
                    .orElse(null);

            tokenEntity.name = token.getName();

            tokenEntity.removed = true;

            /*
             * Un token retiré n'est plus sur le plateau.
             */
            tokenEntity.x = null;
            tokenEntity.y = null;

            entity.tokens.add(tokenEntity);
        });

        return entity;
    }

    /**
     * Conversion GameEntity → Game
     */
    private Game toGame(GameEntity entity) {

        UUID gameId = UUID.fromString(entity.id);

        List<UUID> playerIds = Arrays.stream(entity.playerIds.split(","))
                .filter(id -> !id.isBlank())
                .map(UUID::fromString)
                .toList();

        List<TokenPosition<UUID>> boardTokens = new ArrayList<>();
        List<TokenPosition<UUID>> removedTokens = new ArrayList<>();

        for (GameTokenEntity tokenEntity : entity.tokens) {

            UUID ownerId = tokenEntity.ownerId != null
                    ? UUID.fromString(tokenEntity.ownerId)
                    : null;

            if (tokenEntity.removed) {

                /*
                 * Pour un token retiré, les coordonnées
                 * ne sont pas importantes.
                 */
                removedTokens.add(
                        new TokenPosition<>(
                                ownerId,
                                tokenEntity.name,
                                0,
                                0
                        )
                );

            } else {

                boardTokens.add(
                        new TokenPosition<>(
                                ownerId,
                                tokenEntity.name,
                                tokenEntity.x,
                                tokenEntity.y
                        )
                );
            }
        }

        /*
         * On recherche la factory correspondant
         * au type de jeu enregistré en BDD.
         */
        GameFactory factory = gameFactories.stream()
                .filter(gameFactory ->
                        gameFactory.getGameFactoryId()
                                .equals(entity.factoryId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Aucune GameFactory trouvée pour : "
                                        + entity.factoryId
                        )
                );

        try {

            return factory.createGameWithIds(
                    gameId,
                    entity.boardSize,
                    playerIds,
                    boardTokens,
                    removedTokens
            );

        } catch (InconsistentGameDefinitionException e) {

            throw new IllegalStateException(
                    "Impossible de reconstruire la partie "
                            + entity.id,
                    e
            );
        }
    }
}