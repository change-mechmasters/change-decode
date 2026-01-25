package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class OuttakeServoTest extends OpMode {
    final static double[] INCREMENTS = {0.1, 0.01, 0.001};
    private Servo servo;
    private int currIncrement = 0;
    private double position = 0;

    @Override
    public void init() {
        this.servo = hardwareMap.get(Servo.class, "outtake");
        this.servo.setPosition(this.position);
    }

    @Override
    public void loop() {
        this.servo.setPosition(position);

        telemetry.addData("current increment", INCREMENTS[currIncrement]);
        telemetry.addData("current position", this.position);

        if (gamepad1.leftStickButtonWasPressed()) {
            this.currIncrement = Math.floorMod(this.currIncrement - 1, INCREMENTS.length);
        }
        if (gamepad1.rightStickButtonWasPressed()) {
            this.currIncrement = Math.floorMod(this.currIncrement + 1, INCREMENTS.length);
        }

        if (gamepad1.triangleWasPressed()) {
            this.position += INCREMENTS[currIncrement];
        }
        if (gamepad1.crossWasPressed()) {
            this.position -= INCREMENTS[currIncrement];
        }
    }
}
