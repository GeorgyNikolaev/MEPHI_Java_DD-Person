package ddperson.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "validation.email.required")
        @Email(message = "validation.email.invalid")
        String email,

        @NotBlank(message = "validation.password.required")
        @Size(min = 8, message = "validation.password.min_length")
        String password,

        @NotBlank(message = "validation.display_name.required")
        @Size(max = 100)
        String displayName
) {
}
