package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot;

@Autonomous
public class MainAuto extends OpMode {
    private Robot robot;
    private PathState pathState;
    private final ElapsedTime intakingTimer = new ElapsedTime();
    private final Pose startPose = new Pose(15, 128, Math.toRadians(133));
    private final Pose shootPose = new Pose(48, 95, Math.toRadians(134));
    private final Pose pickup1Start = new Pose(40, 84, Math.toRadians(180));
    private final Pose pickup1End = new Pose(15, pickup1Start.getY(), Math.toRadians(180));
    private final Pose endPose = new Pose(48, 58, Math.toRadians(180));
    private PathChain shootInitial, startGrab1, grab1, shoot1, park;

    private static final double MAX_INTAKING_TIME = 5;

    enum PathState {
        GO_TO_SHOOT_INITIAL,
        SHOOT_INITIAL,
        GO_TO_GRAB1,
        GRAB1,
        GO_TO_SHOOT1,
        SHOOT1,
        GO_TO_END,
        END,
    }


    @Override
    public void init() {
        this.robot = new Robot(hardwareMap, startPose);
        this.shootInitial = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        this.startGrab1 = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(shootPose, pickup1Start))
                .setLinearHeadingInterpolation(shootPose.getHeading(), pickup1Start.getHeading())
                .build();
        this.grab1 = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(pickup1Start, pickup1End))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        this.shoot1 = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(pickup1End, shootPose))
                .setLinearHeadingInterpolation(pickup1End.getHeading(), shootPose.getHeading())
                .build();
        this.park = this.robot.follower.pathBuilder()
                .addPath(new BezierLine(shootPose, endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();
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
                    this.intakingTimer.reset();
                    return PathState.GO_TO_GRAB1;
                }
                else
                    return this.pathState;
            case GO_TO_GRAB1:
                if (this.robot.state == Robot.State.DRIVING) {
                    this.robot.exitShootingState();
                    this.robot.follower.followPath(this.startGrab1);
                    return PathState.GRAB1;
                }
                else
                    return this.pathState;
            case GRAB1:
                if (!this.robot.follower.isBusy()) {
                    this.robot.enterIntakingState();
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
                    this.robot.follower.followPath(this.park);
                    return PathState.END;
                }
                else
                    return this.pathState;
                break;
            case END:
                if (!this.robot.follower.isBusy())
                    this.robot.follower.pausePathFollowing();
                return this.pathState;
        }
    }
}