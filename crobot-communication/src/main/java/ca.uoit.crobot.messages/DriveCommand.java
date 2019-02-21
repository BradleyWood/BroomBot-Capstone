package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class DriveCommand extends Message {

    public enum COMMAND {
        FORWARD,
        BACKWARD,
        LEFT_TURN,
        RIGHT_TURN,
        PROGRAM_START,
        PROGRAM_STOP
    }

    private final int speed;
    private final COMMAND command;

}
