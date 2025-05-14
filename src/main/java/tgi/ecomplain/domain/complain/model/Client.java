package tgi.ecomplain.domain.complain.model;

import lombok.Builder;

@Builder
public record Client(String firstName, String lastName, String email) {
}
