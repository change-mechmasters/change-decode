package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Outtake {
    private static final double UP_POSITION = 0.30;
    private static final double DOWN_POSITION = 0.47;
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

    public boolean isReady() {
        return this.servo.getPosition() == this.position;
    }
    
    private void setPosition(double position) {
        this.position = position;
        this.servo.setPosition(position);
    }
}
