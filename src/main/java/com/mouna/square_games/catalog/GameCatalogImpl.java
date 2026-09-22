package com.mouna.square_games.catalog;

import fr.le_campus_numerique.square_games.engine.GameFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class GameCatalogImpl implements GameCatalog {

    private final List<GameFactory> gameFactories;


    public GameCatalogImpl(List<GameFactory> gameFactories){

        this.gameFactories = gameFactories;
  }

  @Override
    public Collection<String> getGameIds(){

        return gameFactories.stream()
                .map(GameFactory::getGameFactoryId)
                .toList();
  }

  @Override
    public GameFactory getGameFactory(String gameType){

      return gameFactories.stream()
              .filter(factory ->
                      factory.getGameFactoryId().equals(gameType)
              )
              .findFirst()
              .orElse(null);
  }
}
