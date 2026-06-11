package ddperson.domain.enums;

import java.util.Arrays;

public enum UniverseStyle implements LabeledEnum {

    FORGOTTEN_REALMS("FORGOTTEN_REALMS", "Забытые Королевства"),
    EBERRON("EBERRON", "Эберрон"),
    RAVENLOFT("RAVENLOFT", "Равенлофт"),
    DARK_SUN("DARK_SUN", "Тёмное Солнце"),
    PLANESCAPE("PLANESCAPE", "Планскап"),
    CUSTOM("CUSTOM", "Свой стиль");

    private final String code;
    private final String labelRu;

    UniverseStyle(String code, String labelRu) {
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

    public static UniverseStyle fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown UniverseStyle: " + code));
    }
}
