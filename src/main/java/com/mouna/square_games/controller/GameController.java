package com.mouna.square_games.controller;

import com.mouna.square_games.dto.GameCreationParams;
import com.mouna.square_games.dto.MoveRequest;
import com.mouna.square_games.Service.GameService;
import fr.le_campus_numerique.square_games.engine.Game;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.UUID;
@Tag(
        name = "Square_games",
        description = "API de plateau de jeu"
)
@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(
            summary = "Créer une partie",
            description = "Crée une nouvelle partie pour le joueur identifié par X-UserId."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Partie créée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres invalides"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Type de jeu inexistant"
            )
    })
    // Créer une partie
    @PostMapping
    public Game createGame(
            @Parameter(
                    description = "Identifiant du joueur qui crée la partie",
                    required = true
            )
            Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Paramètres nécessaires à la création de la partie",
                    required = true
            )
            @RequestBody GameCreationParams params
    ) {
        String userId = authentication.getName();
        return gameService.createGame(userId, params);
    }

    @Operation(
            summary = "Récupérer une partie",
            description = "Récupère une partie à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Partie trouvée"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Partie introuvable"
            )
    })
    // Récupérer une partie
    @GetMapping("/{gameId}")
    public Game getGame(
            @Parameter(
                    description = "Identifiant unique de la partie",
                    required = true
            )
            @PathVariable UUID gameId
    ) {
        return gameService.getGame(gameId);
    }
    @Operation(
            summary = "Récupérer les parties d'un joueur",
            description = "Récupère les parties associées au joueur identifié par X-UserId."
    )
     @ApiResponse(
             responseCode = "200",
             description = "Liste des parties récupérées"
     )

    @GetMapping
    public Collection<Game> getGames(
            @Parameter(
                    description = "Identifiant du joueur",
                    required = true
            )
            Authentication authentication

    ){
        String userId = authentication.getName();
        return gameService.getGames(userId);
    }

    // Voir les coups possibles
    @Operation(
            summary = "Voir les coups possibles",
            description = "Récupère les coups possibles pour une partie."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Coups possibles récupérés"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Partie introuvable"
            )
    })
    @GetMapping("/{gameId}/moves")
    public Object getPossibleMoves(
            @Parameter(
                    description = "Identifiant unique de la partie",
                    required = true
            )
            @PathVariable UUID gameId
    ) {
        return gameService.getPossibleMoves(gameId);
    }

    // Jouer un coup
    @Operation(
            summary = "Jouer un coup",
            description = "Joue un coup dans une partie pour le joueur identifié par X-UserId."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Coup joué avec succès"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Le joueur n'est pas autorisé à jouer"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Partie introuvable"
            )
    })
    @PostMapping("/{gameId}/moves")
    public Game playMove(
            @Parameter(
                    description = "Identifiant unique de la partie",
                    required = true
            )
            @PathVariable UUID gameId,
            @Parameter(
                    description = "Identifiant du joueur qui joue le coup",
                    required = true
            )
            Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du coup à jouer",
                    required = true
            )
            @RequestBody MoveRequest moveRequest
    ) {
        System.out.println("========== PLAY MOVE CONTROLLER ==========");
        System.out.println("GAME ID = " + gameId);
        System.out.println("AUTH = " + authentication);
        System.out.println("TOKEN NAME = " + moveRequest.tokenName());
        System.out.println("X = " + moveRequest.x());
        System.out.println("Y = " + moveRequest.y());
        System.out.println("==========================================");
        if (authentication == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication absente"
            );
        }

        String userId = authentication.getName();
        return gameService.playMove(gameId, userId, moveRequest);
    }
}
