package net.uniquepixels.support.ticket;

public enum SupportType {

    QUESTION("Frage"),
    BUG("Bug-Report");

    private final String reason;

    SupportType(String reason) {

        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
