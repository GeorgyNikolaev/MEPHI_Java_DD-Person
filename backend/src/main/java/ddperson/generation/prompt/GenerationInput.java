package ddperson.generation.prompt;

import ddperson.domain.enums.Mood;
import ddperson.domain.enums.RoleArchetype;
import ddperson.domain.enums.UniverseStyle;

public record GenerationInput(
        String characterDescription,
        RoleArchetype roleArchetype,
        UniverseStyle universeStyle,
        short seriousnessLevel,
        short expressivenessLevel,
        Mood mood
) {
}
