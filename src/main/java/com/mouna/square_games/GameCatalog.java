package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.GameFactory;

import java.util.Collection;

public interface GameCatalog {
    Collection<String> getGameIds();
    GameFactory getGameFactory(String gameType);
}
