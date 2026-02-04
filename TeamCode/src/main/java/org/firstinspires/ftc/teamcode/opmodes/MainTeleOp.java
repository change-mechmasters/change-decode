package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.BotContext;
import org.firstinspires.ftc.teamcode.Robot;

@TeleOp
public class MainTeleOp extends OpMode {
    private Robot robot;
    private Robot.State lastBotState;
    private int lastArtifactCount;
    private final Gamepad lastGamepad = new Gamepad();

    @Override
    public void init() {
        this.robot = new Robot(hardwareMap, BotContext.botPose);
        BotContext.setGamepadColor(gamepad1);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Current alliance", BotContext.alliance);
    }

    @Override
    public void start() {
        this.robot.follower.startTeleopDrive();
        this.lastBotState = this.robot.state;
        this.lastArtifactCount = this.robot.artifactCount;
    }

    @Override
    public void loop() {
        this.robot.update();

        this.robot.setTeleOpDrive(gamepad1);
        if (gamepad1.rightStickButtonWasPressed())
            this.robot.toggleAutoAim();

        if (gamepad1.circleWasPressed()) {
            if (this.robot.state == Robot.State.DRIVING)
                this.robot.enterShootingState();
            else if (this.robot.state == Robot.State.SHOOTING)
                this.robot.exitShootingState();
        }
        if (gamepad1.right_trigger > 0 && lastGamepad.right_trigger == 0) {
            if (this.robot.state == Robot.State.DRIVING)
                this.robot.enterIntakingState();
        }
        if (gamepad1.right_trigger == 0 && lastGamepad.right_trigger > 0) {
            if (this.robot.state == Robot.State.INTAKING)
                this.robot.exitIntakingState();
        }

        telemetry.addData("State", this.robot.state);
        telemetry.addData("Auto aim", this.robot.autoAim);
        telemetry.addData("Artifact count", this.robot.artifactCount);
        telemetry.addData("Bot pose", this.robot.follower.getPose());

        if (this.lastBotState != this.robot.state) {
            gamepad1.rumble(1, 1, 2000);
        }
        if (this.lastArtifactCount != this.robot.artifactCount) {
            gamepad1.rumble(0.1, 0.1, 500);
        }

        this.lastGamepad.copy(gamepad1);
        this.lastBotState = this.robot.state;
        this.lastArtifactCount = this.robot.artifactCount;
    }
}
