package ddperson.api.mapper;

import ddperson.api.dto.character.CharacterResponse;
import ddperson.api.dto.character.CharacterSummaryResponse;
import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.PortraitSummaryDto;
import ddperson.domain.enums.LabeledEnum;
import ddperson.domain.enums.Mood;
import ddperson.persistence.entity.CharacterEntity;
import ddperson.persistence.entity.PortraitEntity;
import org.springframework.stereotype.Component;

@Component
public class CharacterMapper {

    public CharacterSummaryResponse toSummary(CharacterEntity entity) {
        return new CharacterSummaryResponse(
                entity.getId(),
                entity.getName(),
                toEnumDto(entity.getRoleArchetype()),
                toEnumDto(entity.getUniverseStyle()),
                toPortraitDto(entity.getLastPortrait()),
                entity.getUpdatedAt()
        );
    }

    public CharacterResponse toDetail(CharacterEntity entity) {
        return new CharacterResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                toEnumDto(entity.getRoleArchetype()),
                toEnumDto(entity.getUniverseStyle()),
                entity.getSeriousnessLevel(),
                entity.getExpressivenessLevel(),
                entity.getMood() != null ? toEnumDto(entity.getMood()) : null,
                toPortraitDto(entity.getLastPortrait()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private PortraitSummaryDto toPortraitDto(PortraitEntity portrait) {
        if (portrait == null) {
            return null;
        }
        return new PortraitSummaryDto(
                portrait.getId(),
                "/api/v1/portraits/" + portrait.getId() + "/image",
                portrait.getCreatedAt()
        );
    }

    private EnumLabelDto toEnumDto(LabeledEnum labeled) {
        return new EnumLabelDto(labeled.getCode(), labeled.getLabelRu());
    }

    private EnumLabelDto toEnumDto(Mood mood) {
        return new EnumLabelDto(mood.getCode(), mood.getLabelRu());
    }
}
