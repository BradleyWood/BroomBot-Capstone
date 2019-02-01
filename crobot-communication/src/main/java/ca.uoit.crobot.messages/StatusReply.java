package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public @Data class StatusReply extends Reply {

    private final Map<String, Serializable> attributes;
    private final String version;

}
