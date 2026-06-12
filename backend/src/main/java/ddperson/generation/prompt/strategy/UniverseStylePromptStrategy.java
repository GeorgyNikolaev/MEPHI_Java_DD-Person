package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;
import org.springframework.stereotype.Component;

@Component
public class UniverseStylePromptStrategy implements PromptStrategy {

    @Override
    public String systemFragment(GenerationInput input) {
        return switch (input.universeStyle()) {
            case FORGOTTEN_REALMS -> "Стиль вселенной: Забытые Королевства, классическое высокое фэнтези.";
            case EBERRON -> "Стиль вселенной: Эберрон, магический индустриальный fantasy noir.";
            case RAVENLOFT -> "Стиль вселенной: Равенлофт, готический хоррор и мрачная атмосфера.";
            case DARK_SUN -> "Стиль вселенной: Тёмное Солнце, постапокалиптическое пустынное фэнтези.";
            case PLANESCAPE -> "Стиль вселенной: Планскап, сюрреалистичный мультивселенский fantasy.";
            case CUSTOM -> "Стиль вселенной: авторский fantasy по описанию персонажа.";
        };
    }
}
