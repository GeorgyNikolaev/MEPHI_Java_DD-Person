package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;
import org.springframework.stereotype.Component;

@Component
public class MoodPromptStrategy implements PromptStrategy {

    @Override
    public String systemFragment(GenerationInput input) {
        if (input.mood() == null) {
            return "";
        }
        return "Настроение портрета: " + input.mood().getLabelRu() + ".";
    }

    @Override
    public String userFragment(GenerationInput input) {
        if (input.mood() == null) {
            return "";
        }
        return "Настроение: " + input.mood().getLabelRu() + ".";
    }
}
