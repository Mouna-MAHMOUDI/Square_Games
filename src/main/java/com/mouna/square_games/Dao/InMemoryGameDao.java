package com.mouna.square_games.Dao;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


public class InMemoryGameDao implements GameDao {
    private final Map<String, Game> games = new HashMap<>();

    @Override
    public Stream<Game> findAll(){
        return games.values().stream();
    }

    @Override
    public Optional<Game> findById(String gameId){
        return Optional.ofNullable(games.get(gameId));
    }

    @Override
    public Game upsert(Game game) {
        String gameId = game.getId().toString();
        games.put(gameId, game);
        return game;
    }

    @Override
    public void delete(String gameId) {
        games.remove(gameId);
    }



}
