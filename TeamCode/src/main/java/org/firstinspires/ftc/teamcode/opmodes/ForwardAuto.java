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
public class ForwardAuto extends OpMode {
    private Robot robot;
    private PathState pathState;
    private final ElapsedTime intakingTimer = new ElapsedTime();
    private final Pose startPose = buildPose(20, 122, Math.toRadians(145));
    private final Pose shootPose = buildPose(48, 95, Math.toRadians(134));
    private final Pose pickup1Control = buildPose(62, 77, Math.toRadians(180));
    private final Pose pickup1 = buildPose(22, 80, Math.toRadians(180));
    private final Pose endPose = buildPose(55, 70, Math.toRadians(180));
    private PathChain shootInitial, grab1, shoot1, park;

    private static final double MAX_INTAKING_TIME = 5;

    enum PathState {
        GO_TO_SHOOT_INITIAL,
        SHOOT_INITIAL,
        GRAB1,
        GO_TO_SHOOT1,
        SHOOT1,
        GO_TO_END,
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
                .setConstantHeadingInterpolation(pickup1.getHeading())
                .setVelocityConstraint(3)
                .build();
        this.shoot1 = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(pickup1, shootPose))
                .setLinearHeadingInterpolation(pickup1.getHeading(), shootPose.getHeading())
                .build();
        this.park = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(shootPose, endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
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
                    return PathState.GO_TO_SHOOT1;
                }
                else
                    return this.pathState;
            case GO_TO_SHOOT1:
                if (this.robot.artifactCount == 3 || intakingTimer.seconds() > MAX_INTAKING_TIME) {
                    this.robot.exitIntakingState();
                    this.robot.follower.followPath(this.shoot1);
                    return PathState.SHOOT1;
                }
                else
                    return this.pathState;
            case SHOOT1:
                if (!this.robot.follower.isBusy()) {
                    this.robot.enterShootingState();
                    return PathState.GO_TO_END;
                }
                else
                    return this.pathState;
            case GO_TO_END:
                if (this.robot.state == Robot.State.DRIVING) {
                    this.robot.exitShootingState();
                    this.robot.enterIdlingState();
                    this.robot.follower.followPath(this.park);
                    return PathState.END;
                }
                else
                    return this.pathState;
            case END:
                if (!this.robot.follower.isBusy())
                    this.robot.follower.pausePathFollowing();
                return this.pathState;
        }
        return this.pathState; // This should never run but it errors without it.
    }

    private Pose buildPose(double x, double y, double heading) {
        if (BotContext.alliance == BotContext.Alliance.BLUE)
            return new Pose(x, y, heading);
        else
            return new Pose(144 - x, y, Math.toRadians(180) - heading);
    }
}