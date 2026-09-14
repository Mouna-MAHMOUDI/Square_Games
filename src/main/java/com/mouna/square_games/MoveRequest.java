package com.mouna.square_games;

public record MoveRequest(
        String tokenName,
        int x,
        int y
) {
}
