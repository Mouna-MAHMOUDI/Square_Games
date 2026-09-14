package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.Game;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Créer une partie
    @PostMapping
    public Game createGame(
            @RequestBody GameCreationParams params
    ) {
        return gameService.createGame(params);
    }

    // Récupérer une partie
    @GetMapping("/{gameId}")
    public Game getGame(
            @PathVariable UUID gameId
    ) {
        return gameService.getGame(gameId);
    }

    // Voir les coups possibles
    @GetMapping("/{gameId}/moves")
    public Object getPossibleMoves(
            @PathVariable UUID gameId
    ) {
        return gameService.getPossibleMoves(gameId);
    }

    // Jouer un coup
    @PostMapping("/{gameId}/moves")
    public Game playMove(
            @PathVariable UUID gameId,
            @RequestBody MoveRequest moveRequest
    ) {
        return gameService.playMove(gameId, moveRequest);
    }
}
