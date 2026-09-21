package com.mouna.square_games.Controller;

import com.mouna.square_games.GameCreationParams;
import com.mouna.square_games.MoveRequest;
import com.mouna.square_games.Service.GameService;
import fr.le_campus_numerique.square_games.engine.Game;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
            @RequestHeader("X-UserId") String userId,
            @RequestBody GameCreationParams params
    ) {
        return gameService.createGame(userId, params);
    }

    // Récupérer une partie
    @GetMapping("/{gameId}")
    public Game getGame(
            @PathVariable UUID gameId
    ) {
        return gameService.getGame(gameId);
    }

    @GetMapping
    public Collection<Game> getGames(@RequestHeader("X-UserId") String userId){
        return gameService.getGames(userId);
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
            @RequestHeader("X-UserId") String userId,
            @RequestBody MoveRequest moveRequest
    ) {
        return gameService.playMove(gameId, userId, moveRequest);
    }
}
