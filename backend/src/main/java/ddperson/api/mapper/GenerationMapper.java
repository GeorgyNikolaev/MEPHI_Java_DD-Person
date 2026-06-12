package ddperson.api.mapper;

import ddperson.api.dto.generation.BuiltPromptDto;
import ddperson.api.dto.generation.GenerationDetailResponse;
import ddperson.api.dto.generation.GenerationErrorDto;
import ddperson.api.dto.generation.GenerationParametersDto;
import ddperson.api.dto.generation.GenerationSummaryResponse;
import ddperson.domain.enums.GenerationStatus;
import ddperson.persistence.entity.GenerationParametersEntity;
import ddperson.persistence.entity.GenerationRequestEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerationMapper {

    private final DtoMapper dtoMapper;

    public GenerationMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    public GenerationSummaryResponse toSummary(GenerationRequestEntity entity) {
        return new GenerationSummaryResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getStatus().getLabelRu(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }

    public GenerationDetailResponse toDetail(GenerationRequestEntity entity) {
        GenerationParametersEntity params = entity.getParameters();

        GenerationErrorDto error = null;
        if (entity.getStatus() == GenerationStatus.FAILED) {
            error = new GenerationErrorDto(entity.getErrorCode(), entity.getErrorMessage());
        }

        return new GenerationDetailResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getStatus().getLabelRu(),
                toParametersDto(params),
                new BuiltPromptDto(entity.getBuiltSystemPrompt(), entity.getBuiltUserPrompt()),
                dtoMapper.toPortraitDto(entity.getPortrait()),
                error,
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }

    private GenerationParametersDto toParametersDto(GenerationParametersEntity params) {
        if (params == null) {
            return null;
        }
        return new GenerationParametersDto(
                params.getCharacterDescription(),
                dtoMapper.toEnumDto(params.getRoleArchetype()),
                dtoMapper.toEnumDto(params.getUniverseStyle()),
                params.getSeriousnessLevel(),
                params.getExpressivenessLevel(),
                params.getMood() != null ? dtoMapper.toEnumDto(params.getMood()) : null
        );
    }
}
