package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;
import org.springframework.stereotype.Component;

@Component
public class TonePromptStrategy implements PromptStrategy {

    @Override
    public String systemFragment(GenerationInput input) {
        return "Уровень серьёзности: " + input.seriousnessLevel() + "/10 — "
                + seriousnessHint(input.seriousnessLevel())
                + ". Выразительность: " + input.expressivenessLevel() + "/10 — "
                + expressivenessHint(input.expressivenessLevel()) + ".";
    }

    private String seriousnessHint(short level) {
        if (level <= 3) {
            return "лёгкий, почти комичный тон";
        }
        if (level <= 7) {
            return "сбалансированный реалистичный тон";
        }
        return "серьёзный, драматичный тон";
    }

    private String expressivenessHint(short level) {
        if (level <= 3) {
            return "сдержанная мимика и поза";
        }
        if (level <= 7) {
            return "умеренная эмоциональность";
        }
        return "яркая выразительная поза и детали";
    }
}
