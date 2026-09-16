package com.mouna.square_games;

import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InvalidPositionException;
import fr.le_campus_numerique.square_games.engine.Token;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final List<GamePlugin> gamePlugins;
    private final GameDao gameDao;

    public GameServiceImpl(
            List<GamePlugin> gamePlugins,
            GameDao gameDao
    ) {
        this.gamePlugins = gamePlugins;
        this.gameDao = gameDao;
    }

    /*
     * Recherche le plugin correspondant au type de jeu demandé.
     */
    private GamePlugin getPlugin(String gameType) {

        return gamePlugins.stream()
                .filter(plugin ->
                        plugin.getGameId().equals(gameType)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Type de jeu inconnu : " + gameType
                        )
                );
    }

    /*
     * Création d'une nouvelle partie.
     */
    @Override
    public Game createGame(GameCreationParams params) {

        GamePlugin plugin = getPlugin(params.gameType());

        Game game;

        /*
         * Si aucun paramètre n'est fourni,
         * on utilise les valeurs par défaut du plugin.
         */
        if (params.playerCount() == null
                && params.boardSize() == null) {

            game = plugin.createGame();

        } else {

            /*
             * Les deux paramètres doivent être fournis ensemble.
             */
            if (params.playerCount() == null
                    || params.boardSize() == null) {

                throw new IllegalArgumentException(
                        "playerCount et boardSize doivent être fournis ensemble"
                );
            }

            game = plugin.createGame(
                    params.playerCount(),
                    params.boardSize()
            );
        }

        /*
         * On sauvegarde la partie via le DAO.
         */
        gameDao.upsert(game);

        return game;
    }

    /*
     * Récupération d'une partie existante.
     */
    @Override
    public Game getGame(UUID gameId) {

        return gameDao.findById(gameId.toString())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Partie introuvable : " + gameId
                        )
                );
    }

    /*
     * Retourne les mouvements possibles.
     *
     * Pour le Taquin, on ne peut pas utiliser directement
     * token.getAllowedMoves(), car le moteur fourni contient
     * une logique de voisinage qui ne correspond pas au calcul
     * attendu par notre API.
     *
     * On calcule donc nous-mêmes :
     *
     * 1. quelle est la case vide ;
     * 2. quels tokens sont directement voisins ;
     * 3. ces tokens peuvent se déplacer vers la case vide.
     */
    @Override
    public List<TokenPosition<UUID>> getPossibleMoves(UUID gameId) {

        Game game = getGame(gameId);

        List<TokenPosition<UUID>> possibleMoves =
                new ArrayList<>();

        Map<CellPosition, Token> board =
                game.getBoard();

        /*
         * Recherche de la case vide.
         */
        CellPosition emptyPosition = null;

        for (int y = 0; y < game.getBoardSize(); y++) {

            for (int x = 0; x < game.getBoardSize(); x++) {

                CellPosition position =
                        new CellPosition(x, y);

                /*
                 * Une position absente du board correspond
                 * à la case vide du Taquin.
                 */
                if (!board.containsKey(position)) {

                    emptyPosition = position;
                    break;
                }
            }

            if (emptyPosition != null) {
                break;
            }
        }

        /*
         * Si aucune case vide n'est trouvée,
         * aucun mouvement n'est possible.
         */
        if (emptyPosition == null) {
            return possibleMoves;
        }

        /*
         * Recherche des tokens voisins de la case vide.
         */
        for (Token token : board.values()) {

            CellPosition tokenPosition =
                    token.getPosition();

            if (tokenPosition == null) {
                continue;
            }

            /*
             * Distance de Manhattan.
             *
             * Deux cases sont voisines si :
             *
             * |x1 - x2| + |y1 - y2| == 1
             */
            int distance =
                    Math.abs(
                            tokenPosition.x()
                                    - emptyPosition.x()
                    )
                            +
                            Math.abs(
                                    tokenPosition.y()
                                            - emptyPosition.y()
                            );

            /*
             * Le token peut se déplacer
             * uniquement s'il est directement
             * voisin de la case vide.
             */
            if (distance == 1) {

                UUID owner =
                        token.getOwnerId().orElse(null);

                possibleMoves.add(
                        new TokenPosition<>(
                                owner,
                                token.getName(),
                                emptyPosition.x(),
                                emptyPosition.y()
                        )
                );
            }
        }

        return possibleMoves;
    }

    /*
     * Joue un déplacement.
     */
    @Override
    public Game playMove(
            UUID gameId,
            MoveRequest moveRequest
    ) {

        Game game = getGame(gameId);

        CellPosition destination =
                new CellPosition(
                        moveRequest.x(),
                        moveRequest.y()
                );

        /*
         * ============================================================
         * 1. Vérifier que la destination est dans le plateau
         * ============================================================
         */
        if (moveRequest.x() < 0
                || moveRequest.x() >= game.getBoardSize()
                || moveRequest.y() < 0
                || moveRequest.y() >= game.getBoardSize()) {

            throw new IllegalArgumentException(
                    "Position invalide : " + destination
            );
        }

        /*
         * ============================================================
         * 2. Vérifier que la destination est libre
         * ============================================================
         */
        Map<CellPosition, Token> board =
                game.getBoard();

        if (board.containsKey(destination)) {

            throw new IllegalArgumentException(
                    "La destination est déjà occupée : "
                            + destination
            );
        }

        /*
         * ============================================================
         * 3. Chercher le token dans le BOARD
         *
         * Cas du Taquin :
         * le token est déjà sur le plateau et possède
         * donc une position.
         * ============================================================
         */
        Token tokenToMove = null;
        boolean tokenIsOnBoard = false;

        for (Token token : board.values()) {

            if (token.getName()
                    .equals(moveRequest.tokenName())) {

                tokenToMove = token;
                tokenIsOnBoard = true;
                break;
            }
        }

        /*
         * ============================================================
         * 4. Si le token n'est pas sur le board,
         *    chercher dans les remainingTokens.
         *
         * Cas du Tic-Tac-Toe :
         * les X et les 0 commencent dans remainingTokens.
         * ============================================================
         */
        if (tokenToMove == null) {

            for (Token token : game.getRemainingTokens()) {

                if (token.getName()
                        .equals(moveRequest.tokenName())) {

                    tokenToMove = token;
                    break;
                }
            }
        }

        /*
         * ============================================================
         * 5. Le token n'existe vraiment pas
         * ============================================================
         */
        if (tokenToMove == null) {

            throw new IllegalArgumentException(
                    "Token introuvable : "
                            + moveRequest.tokenName()
            );
        }

        /*
         * ============================================================
         * 6. Vérifier que le token appartient au joueur courant
         *
         * Cette vérification est particulièrement importante
         * pour Tic-Tac-Toe.
         * ============================================================
         */
        UUID tokenOwner =
                tokenToMove.getOwnerId().orElse(null);

        if (game.getCurrentPlayerId() != null
                && tokenOwner != null
                && !tokenOwner.equals(game.getCurrentPlayerId())) {

            throw new IllegalArgumentException(
                    "Ce n'est pas le tour du joueur possédant le token "
                            + moveRequest.tokenName()
            );
        }

        /*
         * ============================================================
         * 7. CAS 1 : TOKEN DÉJÀ SUR LE BOARD
         *
         * C'est le fonctionnement du Taquin.
         * Le token doit être voisin de la destination.
         * ============================================================
         */
        if (tokenIsOnBoard) {

            CellPosition currentPosition =
                    tokenToMove.getPosition();

            if (currentPosition == null) {

                throw new IllegalArgumentException(
                        "Le token n'a pas de position"
                );
            }

            /*
             * Distance de Manhattan.
             */
            int distance =
                    Math.abs(
                            currentPosition.x()
                                    - destination.x()
                    )
                            +
                            Math.abs(
                                    currentPosition.y()
                                            - destination.y()
                            );

            /*
             * Le token doit être directement voisin
             * de la destination.
             */
            if (distance != 1) {

                throw new IllegalArgumentException(
                        "Le token "
                                + moveRequest.tokenName()
                                + " ne peut pas se déplacer vers "
                                + destination
                );
            }
        }

        /*
         * ============================================================
         * 8. Déplacement / placement du token
         *
         * - Taquin : le token change de position.
         * - Tic-Tac-Toe : le token passe de remainingTokens
         *   à une position sur le board.
         * ============================================================
         */
        try {

            tokenToMove.moveTo(destination);

        } catch (InvalidPositionException e) {

            throw new IllegalArgumentException(
                    "Déplacement impossible : "
                            + e.getMessage(),
                    e
            );
        }

        /*
         * ============================================================
         * 9. Sauvegarder la partie en PostgreSQL
         * ============================================================
         */
        gameDao.upsert(game);

        /*
         * ============================================================
         * 10. Retourner la partie mise à jour
         * ============================================================
         */
        return game;
    }
}