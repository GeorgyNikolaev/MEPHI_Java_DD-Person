package ddperson.api.dto.generation;

import ddperson.domain.enums.GenerationStatus;

import java.time.Instant;
import java.util.UUID;

public record GenerationDetailResponse(
        UUID id,
        GenerationStatus status,
        String statusLabel,
        UUID characterId,
        String characterName,
        GenerationParametersDto parameters,
        BuiltPromptDto builtPrompt,
        PortraitSummaryDto portrait,
        GenerationErrorDto error,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {
}
