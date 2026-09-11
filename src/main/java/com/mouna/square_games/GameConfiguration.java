package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class GameConfiguration {

        @Bean
        public GameFactory gameFactory() {
            return new TicTacToeGameFactory();
        }
}
