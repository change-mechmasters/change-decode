package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Outtake {
    // TODO: Reprogram the servo so that up = 1 and down = 0.
    private static final double UP_POSITION = 0.35;
    private static final double DOWN_POSITION = 0.45;
    private final Servo servo;
    private double position;

    public Outtake(HardwareMap hw) {
        this.servo = hw.get(Servo.class, "outtake");
        this.setPosition(DOWN_POSITION);
    }

    public void setUp() {
        this.setPosition(UP_POSITION);
    }

    public void setDown() {
        this.setPosition(DOWN_POSITION);
    }

    public void awaitOuttake() {
        while (this.servo.getPosition() != this.position)
            Util.sleep(Util.POLL_TIME);
    }
    
    private void setPosition(double position) {
        this.position = position;
        this.servo.setPosition(position);
    }
}
