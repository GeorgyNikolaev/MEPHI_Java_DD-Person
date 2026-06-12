package ddperson.api.mapper;

import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.PortraitSummaryDto;
import ddperson.domain.enums.LabeledEnum;
import ddperson.domain.enums.Mood;
import ddperson.persistence.entity.PortraitEntity;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public EnumLabelDto toEnumDto(LabeledEnum labeled) {
        return new EnumLabelDto(labeled.getCode(), labeled.getLabelRu());
    }

    public EnumLabelDto toEnumDto(Mood mood) {
        return new EnumLabelDto(mood.getCode(), mood.getLabelRu());
    }

    public PortraitSummaryDto toPortraitDto(PortraitEntity portrait) {
        if (portrait == null) {
            return null;
        }
        return new PortraitSummaryDto(
                portrait.getId(),
                "/api/v1/portraits/" + portrait.getId() + "/image",
                portrait.getCreatedAt()
        );
    }
}
