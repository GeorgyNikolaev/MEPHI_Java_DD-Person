package ddperson.api.controller;

import ddperson.api.dto.auth.MessageResponse;
import ddperson.api.dto.common.PageResponse;
import ddperson.api.dto.favorite.FavoritePortraitResponse;
import ddperson.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
@Tag(name = "Favorites", description = "Избранные портреты")
@SecurityRequirement(name = "cookieAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    @Operation(summary = "Список избранных портретов")
    public PageResponse<FavoritePortraitResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return favoriteService.list(page, size);
    }
}
