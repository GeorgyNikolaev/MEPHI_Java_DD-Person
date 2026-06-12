package ddperson.api.dto.character;

import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.PortraitSummaryDto;

import java.time.Instant;
import java.util.UUID;

public record CharacterResponse(
        UUID id,
        String name,
        String description,
        EnumLabelDto roleArchetype,
        EnumLabelDto universeStyle,
        short seriousnessLevel,
        short expressivenessLevel,
        EnumLabelDto mood,
        PortraitSummaryDto lastPortrait,
        Instant createdAt,
        Instant updatedAt
) {
}
