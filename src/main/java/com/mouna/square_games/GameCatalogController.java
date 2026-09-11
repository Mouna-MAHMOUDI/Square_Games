package com.mouna.square_games;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class GameCatalogController {

    private final GameCatalog gameCatalog;

    public GameCatalogController(GameCatalog gameCatalog) {
            this.gameCatalog = gameCatalog;
    }

    @GetMapping("/games")
    public Collection<String> getGameIds(){
        return gameCatalog.getGameIds();
    }

   // @PostMapping("/games")

}
