package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;

/**
 * Фрагмент system-промпта для одного аспекта генерации (Strategy).
 * Параметры стиля/роли/настроения — только здесь, не дублируются в user.
 */
public interface PromptStrategy {

    String systemFragment(GenerationInput input);
}
