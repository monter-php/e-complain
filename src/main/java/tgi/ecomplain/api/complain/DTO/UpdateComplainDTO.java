package tgi.ecomplain.api.complain.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplainDTO {
    @NotBlank
    private String message;
    private String country;
    private String clientFirstName;
    private String clientLastName;
}
