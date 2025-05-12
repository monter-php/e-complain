package tgi.ecomplain.api.complain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchComplainRequest {
    private String status; // Will be mapped to ComplainStatus enum
    private String message;
    private String country;
}
