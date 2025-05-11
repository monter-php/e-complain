package tgi.ecomplain.api.complain.DTO;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tgi.ecomplain.domain.complain.ComplainStatus;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ComplainDTO {
    Long complainId;
    ComplainStatus status;
    Integer counter;
}
