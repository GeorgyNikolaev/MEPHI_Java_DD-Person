package ddperson.api.dto.generation;

public record GenerationParametersDto(
        String characterDescription,
        EnumLabelDto roleArchetype,
        EnumLabelDto universeStyle,
        short seriousnessLevel,
        short expressivenessLevel,
        EnumLabelDto mood
) {
}
