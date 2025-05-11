package tgi.ecomplain.domain.complain.model;

import lombok.Builder;
import tgi.ecomplain.domain.complain.ComplainStatus;

import java.util.Date;

@Builder
public record Complain(Integer complainId, String message, Date creationDate, ComplainStatus status, Client client, String country, int counter) {
}
