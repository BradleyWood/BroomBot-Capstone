package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class UpdateRequest extends Request {

    private final String version;
    private final byte[] contents;

    public UpdateRequest(final String version, final byte[] contents) {
        this.version = version;
        this.contents = contents;
    }
}
