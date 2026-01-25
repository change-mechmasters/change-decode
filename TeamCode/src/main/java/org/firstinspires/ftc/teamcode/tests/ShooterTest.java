package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Intake;
import org.firstinspires.ftc.teamcode.Outtake;
import org.firstinspires.ftc.teamcode.Shooter;

// For this test to work properly, TARGET_HEIGHT in Shooter must be set to 0.
@TeleOp
public class ShooterTest extends OpMode {
    final static double[] INCREMENTS = {1, 0.1, 0.01, 0.001};
    private Shooter shooter;
    private Outtake outtake;
    private Intake intake;
    private double distance = 0;
    private int currIncrement = 0;


    @Override
    public void init() {
        this.shooter = new Shooter(hardwareMap);
        this.outtake = new Outtake(hardwareMap);
        this.intake = new Intake(hardwareMap);
    }

    @Override
    public void loop() {
        boolean canShoot = this.shooter.setDesiredVelocity(distance);

        telemetry.addData("current increment", INCREMENTS[this.currIncrement]);
        telemetry.addData("current distance", this.distance);
        telemetry.addData("Possible to shoot", canShoot);
        telemetry.addData("Ready", this.shooter.isReady());

        // For these to work, the desiredVelocity & motor fields in Shooter should be set to public.
        /*
        double desiredVelocity = this.shooter.angularVelocity;
        double currentVelocity = this.shooter.motor.getVelocity(AngleUnit.RADIANS);
        telemetry.addData("desired velocity", desiredVelocity);
        telemetry.addData("current velocity", currentVelocity);
         */

        if (gamepad1.leftStickButtonWasPressed()) {
            currIncrement = Math.floorMod(this.currIncrement - 1, INCREMENTS.length);
        }
        if (gamepad1.rightStickButtonWasPressed()) {
            currIncrement = Math.floorMod(this.currIncrement + 1, INCREMENTS.length);
        }

        if (gamepad1.dpadUpWasPressed()) {
            this.distance += INCREMENTS[currIncrement];
        }
        if (gamepad1.dpadDownWasPressed()) {
            this.distance -= INCREMENTS[currIncrement];
        }

        if (gamepad1.triangleWasPressed()) {
            this.outtake.setUp();
        }
        if (gamepad1.crossWasPressed()) {
            this.outtake.setDown();
        }
        if (gamepad1.squareWasPressed()) {
            this.intake.setEnabled(!this.intake.isEnabled);
        }
        if (gamepad1.circleWasPressed()) {
            this.shooter.setEnabled(!this.shooter.isEnabled);
        }
    }
}
