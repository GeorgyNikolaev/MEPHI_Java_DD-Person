package ddperson.generation.prompt.strategy;

import ddperson.generation.prompt.GenerationInput;

/**
 * Фрагмент промпта для одного аспекта генерации (Strategy).
 */
public interface PromptStrategy {

    String systemFragment(GenerationInput input);

    String userFragment(GenerationInput input);
}
