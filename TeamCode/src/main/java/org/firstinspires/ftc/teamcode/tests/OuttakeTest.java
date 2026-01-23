package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Outtake;

@Disabled
@TeleOp
public class OuttakeTest extends OpMode {
    private Outtake outtake;

    @Override
    public void init() {
        this.outtake = new Outtake(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.triangleWasPressed()) {
            this.outtake.setUp();
        }
        if (gamepad1.crossWasPressed()) {
            this.outtake.setDown();
        }
    }
}
