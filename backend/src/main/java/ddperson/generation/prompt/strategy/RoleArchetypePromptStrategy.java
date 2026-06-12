package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;
import org.springframework.stereotype.Component;

@Component
public class RoleArchetypePromptStrategy implements PromptStrategy {

    @Override
    public String systemFragment(GenerationInput input) {
        return "Роль персонажа: " + input.roleArchetype().getLabelRu()
                + ". Подчеркни атрибуты, типичные для этой роли в D&D.";
    }

    @Override
    public String userFragment(GenerationInput input) {
        return "Архетип/класс: " + input.roleArchetype().getLabelRu() + ".";
    }
}
