package com.mouna.square_games.Service;

import com.mouna.square_games.GameCreationParams;
import com.mouna.square_games.MoveRequest;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.TokenPosition;

import java.util.Collection;
import java.util.UUID;

public interface GameService {

    Game createGame(String userId, GameCreationParams params);

    Collection<Game> getGames(String userId);

    Game getGame(UUID gameId);

    Collection<TokenPosition<UUID>> getPossibleMoves(UUID gameId);

    Game playMove(UUID gameId, String userId, MoveRequest moveRequest);
}
