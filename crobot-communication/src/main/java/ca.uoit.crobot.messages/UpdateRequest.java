package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class UpdateRequest extends Request {

    private final String version;

    public UpdateRequest() {
        this(null);
    }

    public UpdateRequest(final String version) {
        this.version = version;
    }
}
