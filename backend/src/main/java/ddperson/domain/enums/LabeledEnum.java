package ddperson.domain.enums;

/**
 * Enum с техническим кодом (API/БД) и русской подписью для UI.
 */
public interface LabeledEnum {

    String getCode();

    String getLabelRu();
}
