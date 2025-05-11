package tgi.ecomplain.domain.complain;

public enum ComplainStatus {
    SUBMITTED("SUBMITTED"),
    IN_PROGRESS("IN_PROGRESS"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED"),
    RESOLVED("RESOLVED");

    final String value;

    ComplainStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ComplainStatus fromString(String status) {
        return ComplainStatus.valueOf(status);
    }
}
