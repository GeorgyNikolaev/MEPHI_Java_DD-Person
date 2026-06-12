package ddperson.api.dto.generation;

import ddperson.domain.enums.GenerationStatus;

import java.time.Instant;
import java.util.UUID;

public record GenerationSummaryResponse(
        UUID id,
        GenerationStatus status,
        String statusLabel,
        Instant createdAt,
        Instant completedAt
) {
}
