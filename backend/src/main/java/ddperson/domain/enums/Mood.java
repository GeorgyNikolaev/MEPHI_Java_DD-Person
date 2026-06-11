package ddperson.domain.enums;

import java.util.Arrays;

public enum Mood implements LabeledEnum {

    HEROIC("HEROIC", "Героическое"),
    BROODING("BROODING", "Мрачное"),
    WHIMSICAL("WHIMSICAL", "Причудливое"),
    MENACING("MENACING", "Угрожающее"),
    NOBLE("NOBLE", "Благородное"),
    MISCHIEVOUS("MISCHIEVOUS", "Озорное");

    private final String code;
    private final String labelRu;

    Mood(String code, String labelRu) {
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

    public static Mood fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Mood: " + code));
    }
}
