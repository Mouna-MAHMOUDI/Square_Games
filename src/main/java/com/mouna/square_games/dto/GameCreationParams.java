package com.mouna.square_games.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record GameCreationParams (
        @Schema(description = "Type de jeu à créer", example = "tictactoe")
        String gameType,

        @Schema(description = "Nombre de joueurs", example = "2")
        Integer playerCount,

        @Schema(description = "Taille du plateau", example = "3")
        Integer boardSize,

        @Schema(description = "Identifiants des adversaires", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
        List<UUID> opponentIds
){
}
