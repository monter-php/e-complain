package tgi.ecomplain.domain.complain.model;

import lombok.Builder;

import java.util.Date;

@Builder
public record Complain(Long complainId, String message, Date creationDate, String status, Client client, String country, int counter) {
}
