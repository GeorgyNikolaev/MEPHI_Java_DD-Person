package ddperson.api.controller;

import ddperson.api.dto.generation.CreateGenerationRequest;
import ddperson.api.dto.generation.GenerationDetailResponse;
import ddperson.api.dto.generation.GenerationSummaryResponse;
import ddperson.domain.enums.GenerationStatus;
import ddperson.service.GenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/generations")
@Tag(name = "Generations", description = "Генерация портретов персонажей")
@SecurityRequirement(name = "cookieAuth")
public class GenerationController {

    private final GenerationService generationService;

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Создать запрос на генерацию", description = "Асинхронная обработка: статус PENDING → PROCESSING → COMPLETED/FAILED")
    public GenerationSummaryResponse create(@Valid @RequestBody CreateGenerationRequest request) {
        return generationService.create(request);
    }

    @GetMapping
    @Operation(summary = "История запросов")
    public Page<GenerationSummaryResponse> list(
            @RequestParam(required = false) GenerationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return generationService.list(status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детали запроса", description = "Статус, параметры, промпт, портрет или ошибка")
    public GenerationDetailResponse get(@PathVariable UUID id) {
        return generationService.getById(id);
    }

    @PostMapping("/{id}/retry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Повторить генерацию", description = "Создаёт новый запрос с теми же параметрами")
    public GenerationSummaryResponse retry(@PathVariable UUID id) {
        return generationService.retry(id);
    }
}
