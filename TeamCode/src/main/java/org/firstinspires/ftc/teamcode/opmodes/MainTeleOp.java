package org.firstinspires.ftc.teamcode.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.BotContext;
import org.firstinspires.ftc.teamcode.Robot;

@TeleOp
public class MainTeleOp extends OpMode {
    private Robot robot;
    private int lastArtifactCount;
    private boolean lastAimState;

    @Override
    public void init() {
        this.robot = new Robot(hardwareMap, BotContext.botPose);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Current alliance", BotContext.alliance);
    }

    @Override
    public void start() {
        this.robot.follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        this.robot.update();

        this.robot.follower.setTeleOpDrive(
                -gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x
        );

        if (gamepad1.circleWasPressed()) {
            if (this.robot.state == Robot.State.DRIVING)
                this.robot.enterShootingState();
            else if (this.robot.state == Robot.State.SHOOTING)
                this.robot.exitShootingState();
        }
        if (gamepad1.squareWasPressed()) {
            if (this.robot.state == Robot.State.DRIVING)
                this.robot.enterIntakingState();
            else if (this.robot.state == Robot.State.INTAKING)
                this.robot.exitIntakingState();
        }

        Pose botPose = this.robot.follower.getPose();
        telemetry.addData("State", this.robot.state);
        telemetry.addData("Bot pose", botPose);
        telemetry.addData("Artifact count", this.robot.artifactCount);
        telemetry.addData("Desired heading", Math.toDegrees(Robot.getDesiredHeading(botPose)));
        telemetry.addData("Current heading", Math.toDegrees(this.robot.follower.getHeading()));
    }
}
