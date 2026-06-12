package ddperson.generation.prompt.builder;

import ddperson.generation.prompt.BuiltPrompt;
import ddperson.generation.prompt.GenerationInput;
import ddperson.generation.prompt.strategy.PromptStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    private static final String SYSTEM_BASE =
            "Ты — художник-портретист для настольной ролевой игры D&D. "
                    + "Создавай детализированный портрет персонажа (лицо и плечи), fantasy art, "
                    + "высокое качество, без текста и водяных знаков на изображении.";

    private final List<PromptStrategy> strategies;

    public PromptBuilder(List<PromptStrategy> strategies) {
        this.strategies = strategies;
    }

    public BuiltPrompt build(GenerationInput input) {
        String system = strategies.stream()
                .map(s -> s.systemFragment(input))
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));

        String userHints = strategies.stream()
                .map(s -> s.userFragment(input))
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));

        String userPrompt = "Нарисуй портрет персонажа D&D: " + input.characterDescription().trim()
                + ". " + userHints
                + " Один портрет, фокус на лице персонажа.";

        return new BuiltPrompt(SYSTEM_BASE + " " + system, userPrompt);
    }
}
