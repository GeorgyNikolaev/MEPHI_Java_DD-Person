package ddperson.domain.enums;

import java.util.Arrays;

public enum GenerationStatus implements LabeledEnum {

    PENDING("PENDING", "В очереди"),
    PROCESSING("PROCESSING", "Выполняется"),
    COMPLETED("COMPLETED", "Завершено"),
    FAILED("FAILED", "Ошибка");

    private final String code;
    private final String labelRu;

    GenerationStatus(String code, String labelRu) {
        this.code = code;
        this.labelRu = labelRu;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getLabelRu() {
        return labelRu;
    }

    public static GenerationStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown GenerationStatus: " + code));
    }
}
