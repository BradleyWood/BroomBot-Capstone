package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class LidarReply extends Reply {

    private final float[] angles;
    private final float[] ranges;
}
