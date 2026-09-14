package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.TokenPosition;

import java.util.Collection;
import java.util.UUID;

public interface GameService {
    Game createGame(GameCreationParams params);
    Game getGame(UUID gameId);
    Collection<TokenPosition<UUID>> getPossibleMoves(UUID gameId);

    Game playMove(UUID gameId, MoveRequest moveRequest);
}
