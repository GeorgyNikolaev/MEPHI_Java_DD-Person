package ddperson.api.mapper;

import ddperson.domain.enums.RoleArchetype;
import ddperson.domain.enums.UniverseStyle;
import ddperson.persistence.entity.PortraitEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMapperTest {

    private final DtoMapper dtoMapper = new DtoMapper();

    @Test
    void toEnumDto_mapsCodeAndRussianLabel() {
        var dto = dtoMapper.toEnumDto(RoleArchetype.RANGER);

        assertThat(dto.code()).isEqualTo("RANGER");
        assertThat(dto.labelRu()).isEqualTo("Следопыт");
    }

    @Test
    void toPortraitDto_buildsImageUrl() {
        UUID portraitId = UUID.randomUUID();
        PortraitEntity portrait = new PortraitEntity();
        portrait.setId(portraitId);
        portrait.setCreatedAt(Instant.parse("2026-01-01T12:00:00Z"));

        var dto = dtoMapper.toPortraitDto(portrait);

        assertThat(dto.id()).isEqualTo(portraitId);
        assertThat(dto.imageUrl()).isEqualTo("/api/v1/portraits/" + portraitId + "/image");
        assertThat(dto.createdAt()).isEqualTo(portrait.getCreatedAt());
    }

    @Test
    void toPortraitDto_returnsNullForMissingPortrait() {
        assertThat(dtoMapper.toPortraitDto(null)).isNull();
    }
}
