package tgi.ecomplain.domain.complain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public final class Complain {
    private final Long complainId;
    private final String productId;
    private final String message;
    private final Date creationDate;
    private final String status;
    private final Client client;
    private final String country;
    private int counter;
}
