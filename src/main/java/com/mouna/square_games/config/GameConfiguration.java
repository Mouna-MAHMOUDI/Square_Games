package com.mouna.square_games.config;

import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
    public class GameConfiguration {

        @Bean
        public TicTacToeGameFactory ticTacToeGameFactory() {
            return new TicTacToeGameFactory();
        }

        @Bean
        public ConnectFourGameFactory connectFourGameFactory(){
            return new ConnectFourGameFactory();
        }

        @Bean
        public TaquinGameFactory taquinGameFactory(){
            return new TaquinGameFactory();
        }

        @Bean
        public RestClient.Builder restClientBuilder(){
            return RestClient.builder();
        }
}
