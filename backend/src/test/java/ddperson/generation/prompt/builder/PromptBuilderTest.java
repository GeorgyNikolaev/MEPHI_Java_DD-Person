package ddperson.generation.prompt.builder;

import ddperson.domain.enums.Mood;
import ddperson.domain.enums.RoleArchetype;
import ddperson.domain.enums.UniverseStyle;
import ddperson.generation.prompt.BuiltPrompt;
import ddperson.generation.prompt.GenerationInput;
import ddperson.generation.prompt.strategy.MoodPromptStrategy;
import ddperson.generation.prompt.strategy.RoleArchetypePromptStrategy;
import ddperson.generation.prompt.strategy.TonePromptStrategy;
import ddperson.generation.prompt.strategy.UniverseStylePromptStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromptBuilderTest {

    private PromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new PromptBuilder(List.of(
                new UniverseStylePromptStrategy(),
                new RoleArchetypePromptStrategy(),
                new MoodPromptStrategy(),
                new TonePromptStrategy()
        ));
    }

    @Test
    void build_putsStyleParametersOnlyInSystemPrompt() {
        GenerationInput input = new GenerationInput(
                "Эльфийский следопыт со шрамом",
                RoleArchetype.RANGER,
                UniverseStyle.FORGOTTEN_REALMS,
                (short) 7,
                (short) 6,
                Mood.BROODING
        );

        BuiltPrompt prompt = promptBuilder.build(input);

        assertThat(prompt.systemPrompt()).contains("художник-портретист");
        assertThat(prompt.systemPrompt()).contains("Забытые Королевства");
        assertThat(prompt.systemPrompt()).contains("Следопыт");
        assertThat(prompt.systemPrompt()).contains("Мрачное");

        assertThat(prompt.userPrompt()).contains("Эльфийский следопыт со шрамом");
        assertThat(prompt.userPrompt()).doesNotContain("Следопыт");
        assertThat(prompt.userPrompt()).doesNotContain("Забытые Королевства");
    }

    @Test
    void build_omitsMoodFragmentWhenMoodIsNull() {
        GenerationInput input = new GenerationInput(
                "Дварф-воин",
                RoleArchetype.FIGHTER,
                UniverseStyle.DARK_SUN,
                (short) 5,
                (short) 5,
                null
        );

        BuiltPrompt prompt = promptBuilder.build(input);

        assertThat(prompt.systemPrompt()).doesNotContain("Настроение портрета");
        assertThat(prompt.userPrompt()).startsWith("Нарисуй портрет персонажа D&D:");
    }
}
