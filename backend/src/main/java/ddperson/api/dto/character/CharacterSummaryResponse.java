package ddperson.api.dto.character;

import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.PortraitSummaryDto;

import java.time.Instant;
import java.util.UUID;

public record CharacterSummaryResponse(
        UUID id,
        String name,
        EnumLabelDto roleArchetype,
        EnumLabelDto universeStyle,
        PortraitSummaryDto lastPortrait,
        Instant updatedAt
) {
}
