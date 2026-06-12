package ddperson.api.mapper;

import ddperson.api.dto.character.CharacterResponse;
import ddperson.api.dto.character.CharacterSummaryResponse;
import ddperson.persistence.entity.CharacterEntity;
import org.springframework.stereotype.Component;

@Component
public class CharacterMapper {

    private final DtoMapper dtoMapper;

    public CharacterMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    public CharacterSummaryResponse toSummary(CharacterEntity entity) {
        return new CharacterSummaryResponse(
                entity.getId(),
                entity.getName(),
                dtoMapper.toEnumDto(entity.getRoleArchetype()),
                dtoMapper.toEnumDto(entity.getUniverseStyle()),
                dtoMapper.toPortraitDto(entity.getLastPortrait()),
                entity.getUpdatedAt()
        );
    }

    public CharacterResponse toDetail(CharacterEntity entity) {
        return new CharacterResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                dtoMapper.toEnumDto(entity.getRoleArchetype()),
                dtoMapper.toEnumDto(entity.getUniverseStyle()),
                entity.getSeriousnessLevel(),
                entity.getExpressivenessLevel(),
                entity.getMood() != null ? dtoMapper.toEnumDto(entity.getMood()) : null,
                dtoMapper.toPortraitDto(entity.getLastPortrait()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
