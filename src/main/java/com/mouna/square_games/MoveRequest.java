package com.mouna.square_games;

import io.swagger.v3.oas.annotations.media.Schema;

public record MoveRequest(
        @Schema(
                description = "Nom du pion à déplacer",
                example = "X"
        )
        String tokenName,

        @Schema(
                description = "Coordonnée X de la destination",
                example = "1"
        )
        int x,

        @Schema(
                description = "Coordonnée Y de la destination",
                example = "1"
        )
        int y
) {
}
