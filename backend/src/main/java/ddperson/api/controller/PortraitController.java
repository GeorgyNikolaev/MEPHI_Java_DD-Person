package ddperson.api.controller;

import ddperson.api.dto.auth.MessageResponse;
import ddperson.persistence.entity.PortraitEntity;
import ddperson.service.FavoriteService;
import ddperson.service.PortraitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portraits")
@Tag(name = "Portraits", description = "Просмотр сгенерированных портретов")
@SecurityRequirement(name = "cookieAuth")
public class PortraitController {

    private final PortraitService portraitService;
    private final FavoriteService favoriteService;

    public PortraitController(PortraitService portraitService, FavoriteService favoriteService) {
        this.portraitService = portraitService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/{id}/image")
    @Operation(summary = "Изображение портрета (JPG)")
    public ResponseEntity<byte[]> image(@PathVariable UUID id) {
        PortraitEntity portrait = portraitService.getOwnedPortrait(id);
        byte[] bytes = portraitService.readImage(portrait);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + ".jpg\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @PostMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить портрет в избранное")
    public MessageResponse addToFavorites(@PathVariable UUID id) {
        return favoriteService.add(id);
    }

    @DeleteMapping("/{id}/favorite")
    @Operation(summary = "Убрать портрет из избранного")
    public MessageResponse removeFromFavorites(@PathVariable UUID id) {
        return favoriteService.remove(id);
    }
}
