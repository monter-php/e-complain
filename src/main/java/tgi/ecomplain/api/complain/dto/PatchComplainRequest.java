package tgi.ecomplain.api.complain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgi.ecomplain.api.complain.validation.ValidComplainStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchComplainRequest {
    @NotBlank
    @ValidComplainStatus
    private String status;
    private String message;
    private String country;
}
