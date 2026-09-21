package com.mouna.square_games.plugin;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public class ConnectFourPlugin implements GamePlugin {

    private final ConnectFourGameFactory gameFactory;
    private final MessageSource messageSource;

    private final int defaultPlayerCount;
    private final int defaultBoardSize;

    public ConnectFourPlugin(
            ConnectFourGameFactory gameFactory,
            MessageSource messageSource,
            @Value("${game.connect4.default-player-count}") int defaultPlayerCount,
            @Value("${game.connect4.default-board-size}") int defaultBoardSize
    ) {
        this.gameFactory = gameFactory;
        this.messageSource = messageSource;
        this.defaultPlayerCount = defaultPlayerCount;
        this.defaultBoardSize = defaultBoardSize;
    }

    @Override
    public String getGameId() {
        return gameFactory.getGameFactoryId();
    }

    @Override
    public Game createGame(int playerCount, int boardSize) {
         return gameFactory.createGame(playerCount, boardSize);
    }

    @Override
    public Game createGame(int boardSize, Set<UUID> playerIds) {
        return gameFactory.createGame(boardSize, playerIds);
    }

    @Override
    public Game createGame() {
        return gameFactory.createGame(
                defaultPlayerCount,
                defaultBoardSize
        );
    }

    @Override
    public String getName(Locale locale) {
        return messageSource.getMessage(
                "game.connect4.name",
                null,
                locale
        );
    }
}