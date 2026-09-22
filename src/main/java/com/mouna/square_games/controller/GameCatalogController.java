package com.mouna.square_games.controller;

import com.mouna.square_games.catalog.GameInfo;
import com.mouna.square_games.plugin.GamePlugin;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Tag(
        name = "Users",
        description = "API de gestion des utilisateurs"
)
@RestController
public class GameCatalogController {

    private final List<GamePlugin> gamePlugins;

    public GameCatalogController(List<GamePlugin> gamePlugins) {
        this.gamePlugins = gamePlugins;
    }

    @GetMapping("/games/catalog")
    public List<GameInfo> getGames(
            @RequestHeader(
                    value = "Accept-Language",
                    required = false,
                    defaultValue = "en"
            )
            String language
    ) {

        Locale locale = Locale.forLanguageTag(language);

        return gamePlugins.stream()
                .map(plugin ->
                        new GameInfo(
                                plugin.getGameId(),
                                plugin.getName(locale)
                        )
                )
                .toList();
    }
}