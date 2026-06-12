package ddperson.api.dto.favorite;

import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.PortraitSummaryDto;

import java.time.Instant;
import java.util.UUID;

public record FavoritePortraitResponse(
        UUID id,
        PortraitSummaryDto portrait,
        UUID generationId,
        String characterDescription,
        EnumLabelDto roleArchetype,
        EnumLabelDto universeStyle,
        UUID characterId,
        String characterName,
        Instant favoritedAt
) {
}
