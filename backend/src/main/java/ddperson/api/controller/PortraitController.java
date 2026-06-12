package ddperson.api.controller;

import ddperson.persistence.entity.PortraitEntity;
import ddperson.service.PortraitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portraits")
@Tag(name = "Portraits", description = "Просмотр сгенерированных портретов")
@SecurityRequirement(name = "cookieAuth")
public class PortraitController {

    private final PortraitService portraitService;

    public PortraitController(PortraitService portraitService) {
        this.portraitService = portraitService;
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
}
