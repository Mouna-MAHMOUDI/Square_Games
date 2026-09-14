package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TicTacToePlugin implements GamePlugin {

    private final TicTacToeGameFactory gameFactory;
    private final MessageSource messageSource;

    private final int defaultPlayerCount;
    private final int defaultBoardSize;

    public TicTacToePlugin(
            TicTacToeGameFactory gameFactory,
            MessageSource messageSource,
            @Value("${game.tictactoe.default-player-count}") int defaultPlayerCount,
            @Value("${game.tictactoe.default-board-size}") int defaultBoardSize
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
    public Game createGame() {
        return gameFactory.createGame(
                defaultPlayerCount,
                defaultBoardSize
        );
    }

    @Override
    public String getName(Locale locale) {
        return messageSource.getMessage(
                "game.tictactoe.name",
                null,
                locale
        );
    }
}