package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;
import org.springframework.stereotype.Component;

@Component
public class UniverseStylePromptStrategy implements PromptStrategy {

    @Override
    public String systemFragment(GenerationInput input) {
        return switch (input.universeStyle()) {
            case FORGOTTEN_REALMS -> "Стиль: Forgotten Realms, классическое высокое фэнтези.";
            case EBERRON -> "Стиль: Eberron, магический индустриальный fantasy noir.";
            case RAVENLOFT -> "Стиль: Ravenloft, готический хоррор и мрачная атмосфера.";
            case DARK_SUN -> "Стиль: Dark Sun, постапокалиптическое пустынное фэнтези.";
            case PLANESCAPE -> "Стиль: Planescape, сюрреалистичный мультивселенский fantasy.";
            case CUSTOM -> "Стиль: авторский fantasy по описанию персонажа.";
        };
    }

    @Override
    public String userFragment(GenerationInput input) {
        return "Визуальный стиль вселенной: " + input.universeStyle().getLabelRu() + ".";
    }
}
