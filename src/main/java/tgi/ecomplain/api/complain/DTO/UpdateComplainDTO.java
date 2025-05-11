package tgi.ecomplain.api.complain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplainDTO {
    private String message;
    private String country;
    private String clientEmail;
    private String clientFirstName;
    private String clientLastName;
}
