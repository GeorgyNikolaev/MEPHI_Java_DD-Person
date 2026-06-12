package ddperson.api.mapper;

import ddperson.api.dto.favorite.FavoritePortraitResponse;
import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.PortraitSummaryDto;
import ddperson.domain.enums.LabeledEnum;
import ddperson.domain.enums.Mood;
import ddperson.persistence.entity.FavoritePortraitEntity;
import ddperson.persistence.entity.GenerationParametersEntity;
import ddperson.persistence.entity.GenerationRequestEntity;
import ddperson.persistence.entity.PortraitEntity;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoritePortraitResponse toResponse(FavoritePortraitEntity favorite) {
        PortraitEntity portrait = favorite.getPortrait();
        GenerationRequestEntity request = portrait.getRequest();
        GenerationParametersEntity params = request != null ? request.getParameters() : null;

        EnumLabelDto role = params != null ? toEnumDto(params.getRoleArchetype()) : null;
        EnumLabelDto universe = params != null ? toEnumDto(params.getUniverseStyle()) : null;
        String description = params != null ? params.getCharacterDescription() : null;

        var character = request != null ? request.getCharacter() : null;

        return new FavoritePortraitResponse(
                favorite.getId(),
                new PortraitSummaryDto(
                        portrait.getId(),
                        "/api/v1/portraits/" + portrait.getId() + "/image",
                        portrait.getCreatedAt()
                ),
                description,
                role,
                universe,
                character != null ? character.getId() : null,
                character != null ? character.getName() : null,
                favorite.getCreatedAt()
        );
    }

    private EnumLabelDto toEnumDto(LabeledEnum labeled) {
        return new EnumLabelDto(labeled.getCode(), labeled.getLabelRu());
    }

    private EnumLabelDto toEnumDto(Mood mood) {
        return new EnumLabelDto(mood.getCode(), mood.getLabelRu());
    }
}
