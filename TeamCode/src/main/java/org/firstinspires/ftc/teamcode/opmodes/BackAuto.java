package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BotContext;
import org.firstinspires.ftc.teamcode.Robot;

@Autonomous
public class BackAuto extends OpMode {
    private Robot robot;
    private PathState pathState;
    private final ElapsedTime intakingTimer = new ElapsedTime();
    private final Pose startPose = buildPose(48, 8, Math.toRadians(90));
    private final Pose shootPose = buildPose(60, 20, Math.toRadians(116));
    private final Pose pickup1Control = buildPose(85, 35, Math.toRadians(180));
    private final Pose pickup1 = buildPose(9, 36, Math.toRadians(180));
    private PathChain shootInitial, grab1;

    enum PathState {
        GO_TO_SHOOT_INITIAL,
        SHOOT_INITIAL,
        GRAB1,
        END,
    }

    @Override
    public void init() {
        this.robot = new Robot(hardwareMap, startPose);
        this.robot.follower.setMaxPower(0.5);
        BotContext.setGamepadColor(gamepad1);

        this.shootInitial = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        this.grab1 = this.robot.follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, pickup1Control, pickup1))
                .setLinearHeadingInterpolation(shootPose.getHeading(), pickup1.getHeading())
                .setVelocityConstraint(3)
                .build();
    }

    @Override
    public void init_loop() {
        telemetry.addData("Current alliance", BotContext.alliance);
    }

    @Override
    public void start() {
        this.pathState = PathState.GO_TO_SHOOT_INITIAL;
    }

    @Override
    public void loop() {
        this.robot.update();
        this.pathState = this.autonomousPathUpdate();
        telemetry.addData("Path state", this.pathState);
        telemetry.addData("Robot state", this.robot.state);
        telemetry.addData("Bot pose", this.robot.follower.getPose());
        telemetry.addData("Artifact count", this.robot.artifactCount);
    }

    private PathState autonomousPathUpdate() {
        switch (this.pathState) {
            case GO_TO_SHOOT_INITIAL:
                this.robot.follower.followPath(this.shootInitial);
                return PathState.SHOOT_INITIAL;
            case SHOOT_INITIAL:
                if (!this.robot.follower.isBusy()) {
                    this.robot.enterShootingState();
                    return PathState.GRAB1;
                }
                else
                    return this.pathState;
            case GRAB1:
                if (this.robot.state == Robot.State.DRIVING) {
                    this.robot.enterIntakingState();
                    this.intakingTimer.reset();
                    this.robot.follower.followPath(this.grab1);
                    return PathState.END;
                }
                else
                    return this.pathState;
            case END:
                if (!this.robot.follower.isBusy()) {
                    this.robot.follower.pausePathFollowing();
                    // SAFETY: This state transition will be called repeatedly, which is generally
                    //  bad, but it doesn't matter in this case and it'll take too much effort
                    //  to fix it.
                    this.robot.enterIdlingState();
                }
                return this.pathState;
        }
        return this.pathState; // This should never run but it errors otherwise.
    }

    private Pose buildPose(double x, double y, double heading) {
        if (BotContext.alliance == BotContext.Alliance.BLUE)
            return new Pose(x, y, heading);
        else
            return new Pose(144 - x, y, Math.toRadians(180) - heading);
    }
}
