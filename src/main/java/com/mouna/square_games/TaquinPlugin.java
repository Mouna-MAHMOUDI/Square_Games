package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TaquinPlugin implements GamePlugin {

    private final TaquinGameFactory gameFactory;
    private final MessageSource messageSource;

    private final int defaultPlayerCount;
    private final int defaultBoardSize;

    public TaquinPlugin(
            TaquinGameFactory gameFactory,
            MessageSource messageSource,
            @Value("${game.taquin.default-player-count}") int defaultPlayerCount,
            @Value("${game.taquin.default-board-size}") int defaultBoardSize
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
    public Game createGame(
            int playerCount,
            int boardSize
    ) {
        return gameFactory.createGame(
                playerCount,
                boardSize
        );
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
                "game.taquin.name",
                null,
                locale
        );
    }
}