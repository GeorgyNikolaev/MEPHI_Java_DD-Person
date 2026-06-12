package ddperson.api.controller;

import ddperson.api.dto.character.CharacterResponse;
import ddperson.api.dto.character.CharacterSummaryResponse;
import ddperson.api.dto.character.CreateCharacterRequest;
import ddperson.api.dto.character.UpdateCharacterRequest;
import ddperson.api.dto.common.PageResponse;
import ddperson.api.dto.generation.GenerationSummaryResponse;
import ddperson.service.CharacterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/characters")
@Tag(name = "Characters", description = "Шаблоны персонажей для повторной генерации")
@SecurityRequirement(name = "cookieAuth")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать персонажа", description = "Сохраняет шаблон с параметрами генерации")
    public CharacterResponse create(@Valid @RequestBody CreateCharacterRequest request) {
        return characterService.create(request);
    }

    @GetMapping
    @Operation(summary = "Список персонажей")
    public PageResponse<CharacterSummaryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return characterService.list(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детали персонажа")
    public CharacterResponse get(@PathVariable UUID id) {
        return characterService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить персонажа")
    public CharacterResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCharacterRequest request) {
        return characterService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить персонажа")
    public void delete(@PathVariable UUID id) {
        characterService.delete(id);
    }

    @PostMapping("/{id}/generations")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Сгенерировать портрет по шаблону", description = "Создаёт запрос генерации с параметрами персонажа")
    public GenerationSummaryResponse generate(@PathVariable UUID id) {
        return characterService.generatePortrait(id);
    }
}
