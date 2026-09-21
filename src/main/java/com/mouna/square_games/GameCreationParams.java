package com.mouna.square_games;

import java.util.List;
import java.util.UUID;

public record GameCreationParams (
        String gameType,
        Integer playerCount,
        Integer boardSize,
        List<UUID> opponentIds
){
}
