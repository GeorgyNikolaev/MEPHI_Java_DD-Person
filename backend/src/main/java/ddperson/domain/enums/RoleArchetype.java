package ddperson.domain.enums;

import java.util.Arrays;

public enum RoleArchetype implements LabeledEnum {

    BARBARIAN("BARBARIAN", "Варвар"),
    BARD("BARD", "Бард"),
    CLERIC("CLERIC", "Жрец"),
    DRUID("DRUID", "Друид"),
    FIGHTER("FIGHTER", "Воин"),
    MONK("MONK", "Монах"),
    PALADIN("PALADIN", "Паладин"),
    RANGER("RANGER", "Следопыт"),
    ROGUE("ROGUE", "Плут"),
    SORCERER("SORCERER", "Чародей"),
    WARLOCK("WARLOCK", "Колдун"),
    WIZARD("WIZARD", "Волшебник");

    private final String code;
    private final String labelRu;

    RoleArchetype(String code, String labelRu) {
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

    public static RoleArchetype fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown RoleArchetype: " + code));
    }
}
