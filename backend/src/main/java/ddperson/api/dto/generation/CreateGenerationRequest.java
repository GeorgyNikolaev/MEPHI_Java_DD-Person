package ddperson.api.dto.generation;

import ddperson.domain.enums.Mood;
import ddperson.domain.enums.RoleArchetype;
import ddperson.domain.enums.UniverseStyle;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGenerationRequest(
        @NotBlank(message = "validation.character_description.required")
        @Size(max = 5000)
        String characterDescription,

        @NotNull(message = "validation.role_archetype.required")
        RoleArchetype roleArchetype,

        @NotNull(message = "validation.universe_style.required")
        UniverseStyle universeStyle,

        @Min(value = 1, message = "validation.seriousness_level.range")
        @Max(value = 10, message = "validation.seriousness_level.range")
        short seriousnessLevel,

        @Min(value = 1, message = "validation.expressiveness_level.range")
        @Max(value = 10, message = "validation.expressiveness_level.range")
        short expressivenessLevel,

        Mood mood
) {
}
