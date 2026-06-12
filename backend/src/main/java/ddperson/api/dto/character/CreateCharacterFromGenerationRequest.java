package ddperson.api.dto.character;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCharacterFromGenerationRequest(
        @NotBlank(message = "validation.character_name.required")
        @Size(max = 150)
        String name
) {
}
