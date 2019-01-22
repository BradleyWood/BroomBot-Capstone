package ca.uoit.crobot.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class DriveCommand extends Message {

    private final int leftMotorSpeed;
    private final int rightMotorSpeed;

}
