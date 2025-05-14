package tgi.ecomplain.api.complain.dto;

import java.util.Date;

public record ComplainDetailResponse(
    Long complainId,
    String productId,
    String message,
    Date creationDate,
    String status,
    ClientResponse client,
    String country,
    int counter
) {
}
