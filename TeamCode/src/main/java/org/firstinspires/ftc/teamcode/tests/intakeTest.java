package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Intake;

@TeleOp(group = "Tests")
public class intakeTest extends OpMode {
    private Intake intake;

    @Override
    public void init() {
        this.intake = new Intake(hardwareMap);
    }

    @Override
    public void start() {
        this.intake.setEnabled(true);
    }

    @Override
    public void loop() {
        if (gamepad1.crossWasPressed()) {
            this.intake.setEnabled(!this.intake.isEnabled);
        }
    }
}
