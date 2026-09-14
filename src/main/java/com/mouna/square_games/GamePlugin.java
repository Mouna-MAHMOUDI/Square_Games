package com.mouna.square_games;
import fr.le_campus_numerique.square_games.engine.Game;
import java.util.Locale;

public interface GamePlugin {
    String getGameId();

    Game createGame(int playerCount, int boardSize);

    Game createGame();

    String getName(Locale locale);


}
