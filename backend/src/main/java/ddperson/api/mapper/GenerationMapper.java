package ddperson.api.mapper;

import ddperson.api.dto.generation.BuiltPromptDto;
import ddperson.api.dto.generation.EnumLabelDto;
import ddperson.api.dto.generation.GenerationDetailResponse;
import ddperson.api.dto.generation.GenerationErrorDto;
import ddperson.api.dto.generation.GenerationParametersDto;
import ddperson.api.dto.generation.GenerationSummaryResponse;
import ddperson.api.dto.generation.PortraitSummaryDto;
import ddperson.domain.enums.GenerationStatus;
import ddperson.domain.enums.LabeledEnum;
import ddperson.domain.enums.Mood;
import ddperson.persistence.entity.GenerationParametersEntity;
import ddperson.persistence.entity.GenerationRequestEntity;
import ddperson.persistence.entity.PortraitEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerationMapper {

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
        PortraitEntity portrait = entity.getPortrait();

        GenerationErrorDto error = null;
        if (entity.getStatus() == GenerationStatus.FAILED) {
            error = new GenerationErrorDto(entity.getErrorCode(), entity.getErrorMessage());
        }

        PortraitSummaryDto portraitDto = null;
        if (portrait != null) {
            portraitDto = new PortraitSummaryDto(
                    portrait.getId(),
                    "/api/v1/portraits/" + portrait.getId() + "/image",
                    portrait.getCreatedAt()
            );
        }

        var character = entity.getCharacter();
        UUID characterId = character != null ? character.getId() : null;
        String characterName = character != null ? character.getName() : null;

        return new GenerationDetailResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getStatus().getLabelRu(),
                characterId,
                characterName,
                toParametersDto(params),
                new BuiltPromptDto(entity.getBuiltSystemPrompt(), entity.getBuiltUserPrompt()),
                portraitDto,
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
                toEnumDto(params.getRoleArchetype()),
                toEnumDto(params.getUniverseStyle()),
                params.getSeriousnessLevel(),
                params.getExpressivenessLevel(),
                params.getMood() != null ? toEnumDto(params.getMood()) : null
        );
    }

    private EnumLabelDto toEnumDto(LabeledEnum labeled) {
        return new EnumLabelDto(labeled.getCode(), labeled.getLabelRu());
    }

    private EnumLabelDto toEnumDto(Mood mood) {
        return new EnumLabelDto(mood.getCode(), mood.getLabelRu());
    }
}
