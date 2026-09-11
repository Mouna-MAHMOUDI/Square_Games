package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.GameFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class GameCatalogImpl implements GameCatalog{

    private final GameFactory gameFactory;

    public GameCatalogImpl(GameFactory gameFactory){

        this.gameFactory = gameFactory;
  }

  @Override
    public Collection<String> getGameIds(){

        return List.of(gameFactory.getGameFactoryId());
  }
}
