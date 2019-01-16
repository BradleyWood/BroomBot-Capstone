package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract @Data class Reply extends Message {

    private final boolean success;
    private String errorMessage;

}
