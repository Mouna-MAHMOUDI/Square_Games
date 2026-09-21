package com.mouna.square_games.plugin;
import fr.le_campus_numerique.square_games.engine.Game;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public interface GamePlugin {
    String getGameId();

    Game createGame(int playerCount, int boardSize);

    Game createGame();

    Game createGame(int boardSize, Set<UUID> playerIds);

    String getName(Locale locale);


}
