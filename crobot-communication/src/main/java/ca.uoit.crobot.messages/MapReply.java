package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class MapReply extends Reply {

    private final int MAP_SIZE_PIXELS;
    private final int MAP_SIZE_METERS;
    private final byte[] map;
    private final int x;
    private final int y;

}
