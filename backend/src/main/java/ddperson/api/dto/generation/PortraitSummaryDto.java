package ddperson.api.dto.generation;

import java.time.Instant;
import java.util.UUID;

public record PortraitSummaryDto(
        UUID id,
        String imageUrl,
        Instant createdAt
) {
}
