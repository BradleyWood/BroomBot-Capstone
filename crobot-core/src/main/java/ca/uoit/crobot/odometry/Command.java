package ca.uoit.crobot.odometry;

public class Command {

    enum CommandType { DRIVE, TURN_LEFT, TURN_RIGHT; }

    final CommandType commandType;
    final int speed;
    final int distance;

    public Command(CommandType commandType, int speed, int distance, int turnAngle) {
        this.commandType = commandType;
        this.speed = speed;
        this.distance = distance;
    }

}
