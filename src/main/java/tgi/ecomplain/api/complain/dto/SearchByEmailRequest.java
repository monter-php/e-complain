package tgi.ecomplain.api.complain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SearchByEmailRequest(
        @Schema(description = "Email address of the client", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email cannot be blank")
        String email
) {
}
