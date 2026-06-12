package ddperson.api.mapper;

import ddperson.api.dto.favorite.FavoritePortraitResponse;
import ddperson.persistence.entity.FavoritePortraitEntity;
import ddperson.persistence.entity.GenerationParametersEntity;
import ddperson.persistence.entity.GenerationRequestEntity;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    private final DtoMapper dtoMapper;

    public FavoriteMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    public FavoritePortraitResponse toResponse(FavoritePortraitEntity favorite) {
        var portrait = favorite.getPortrait();
        GenerationRequestEntity request = portrait.getRequest();
        GenerationParametersEntity params = request != null ? request.getParameters() : null;

        return new FavoritePortraitResponse(
                favorite.getId(),
                dtoMapper.toPortraitDto(portrait),
                request != null ? request.getId() : null,
                params != null ? params.getCharacterDescription() : null,
                params != null ? dtoMapper.toEnumDto(params.getRoleArchetype()) : null,
                params != null ? dtoMapper.toEnumDto(params.getUniverseStyle()) : null,
                favorite.getCreatedAt()
        );
    }
}
