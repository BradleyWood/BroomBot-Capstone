package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class Reply extends Message {

    private final boolean success;
    private final String errorMessage;

    public Reply(final boolean success, final String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public Reply(final boolean success) {
        this(success, null);
    }

    public Reply() {
        this(true);
    }

}
