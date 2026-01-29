package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class Robot {
    public State state;
    public Follower follower;
    public int artifactCount;
    private ShooterState shooterState;
    private final Intake intake;
    private final ArtifactSensor artifactSensor;
    private final Outtake outtake;
    private final Shooter shooter;

    // DECODE Competition Manual
    private static final double FIELD_DISTANCE_CONVERSION_RATE = 3.6 / 144.0;
    private static final double GOAL_OPENING_CENTER_DISTANCE = 0.4645 / 2;
    private static final double MAX_AIM_ERROR = Math.toRadians(7.5);
    private static Pose GOAL;
    private static final Pose BLUE_GOAL = new Pose(0, 144);
    private static final Pose RED_GOAL = new Pose(144, 144);

    public enum State {
        DRIVING,
        SHOOTING,
        INTAKING,
        //PARKING, // TODO: Think about an automatic parking state
    }

    private enum ShooterState {
        PREPARING,
        SHOOTING,
    }

    public Robot(HardwareMap hw, Pose initialPose, int initialArtifactCount) {
        this.intake = new Intake(hw);
        this.artifactSensor = new ArtifactSensor(hw);
        this.outtake = new Outtake(hw);
        this.shooter = new Shooter(hw);

        this.follower = Constants.createFollower(hw);
        this.follower.setStartingPose(initialPose);
        follower.update();

        this.state = State.DRIVING;

        this.artifactCount = initialArtifactCount;

        if (BotContext.alliance == BotContext.Alliance.BLUE)
            GOAL = BLUE_GOAL;
        else
            GOAL = RED_GOAL;
    }

    public void update() {
        follower.update();
        switch (this.state) {
            case DRIVING:
                this.handleDrivingState();
                break;
            case SHOOTING:
                this.handleShootingState();
                break;
            case INTAKING:
                this.handleIntakingState();
                break;
        }
    }

    // SAFETY: It is expected that these state transition functions
    //  will be called in the right state, otherwise the correct state transitions
    //  may not get triggered and everything will come to ruin.
    public void enterShootingState() {
        this.shooterState = ShooterState.PREPARING;
        this.state = State.SHOOTING;
    }

    public void exitShootingState() {
        this.outtake.setDown();
        //this.follower.pausePathFollowing();
        this.state = State.DRIVING;
    }

    public void enterIntakingState() {
        this.state = State.INTAKING;
    }

    public void exitIntakingState() {
        this.state = State.DRIVING;
    }

    // SAFETY: It's expected that these functions too, like above, will be called in the right state.
    //  I have omitted redundant checks, because something would have already went wrong
    //  if the function is getting called in the first place.
    private void handleDrivingState() {
        // STRATEGY: It isn't intended that we'll collect artifacts in this state,
        // but it isn't a harm if we do.
        this.intake.setEnabled(true);
        // STRATEGY: The shooter can decelerate almost immediately, but it takes
        //  a few seconds to accelerate, so it's better to start accelerating from now.
        this.shooter.setEnabled(true);
        this.shooter.setDesiredVelocity(this.getDistanceToGoal());
    }

    private void handleShootingState() {
        // STRATEGY: The intake is also the transfer, so it must be on while shooting.
        //  We could turn it on and off, providing more voltage to the other mechanisms,
        //  but this is likely faster since there will already be a force on the artifact
        //  as soon as the outtake goes down.
        // TODO: Fix aim. Current problems:
        //  - I don't know how to make it stop turning, giving control back to the OpMode
        //  - It takes a longer path for turning for BLUE
        //  - It would be cool if it could be sped up
        this.intake.setEnabled(true);
        switch (this.shooterState) {
            case PREPARING:
                // STRATEGY: Shooter acceleration is not the bottleneck; the intake is.
                //  Hence, incremental distance changes are not necessary.
                final double distance = this.getDistanceToGoal() - GOAL_OPENING_CENTER_DISTANCE;
                final double heading = this.getDesiredHeading();
                //this.follower.turnTo(heading);
                this.shooter.setDesiredVelocity(distance);
                this.outtake.setDown();
                if (this.shooter.isReady() && this.artifactSensor.isArtifactPresent() /*&& this.isAimReady()*/)
                    this.shooterState = ShooterState.SHOOTING;
                break;
            case SHOOTING:
                this.outtake.setUp();
                if (!this.artifactSensor.isArtifactPresent()) {
                    this.artifactCount -= 1;
                    this.shooterState = ShooterState.PREPARING;
                }
                break;
        }

        if (this.artifactCount == 0)
            this.exitShootingState();
    }

    private void handleIntakingState() {
        // TODO: Automated intaking state. This requires:
        //  - Upgrading ArtifactSensor into ArtifactSensors, allowing for
        //    knowing how many artifacts are in storage at any moment.
        //  - Adding automatic camera-detection of artifacts, allowing for
        //    the bot to automatically charge to them.
    }

    private double getDistanceToGoal() {
        final Pose botPose = this.follower.getPose();
        final double x_diff = GOAL.getX() - botPose.getX();
        final double y_diff = GOAL.getY() - botPose.getY();
        final double distance_ticks = Math.sqrt(x_diff*x_diff + y_diff*y_diff);
        return distance_ticks * FIELD_DISTANCE_CONVERSION_RATE;
    }

    public double getDesiredHeading() {
        final Pose botPose = this.follower.getPose();
        final double x_diff = GOAL.getX() - botPose.getX();
        final double y_diff = GOAL.getY() - botPose.getY();
        final double denominator = Math.sqrt(x_diff*x_diff + y_diff*y_diff);
        final double angle = Math.acos(Math.abs(x_diff) / denominator);
        if (botPose.getX() > GOAL.getX())
            return Math.toRadians(180) - angle;
        else
            return angle;
    }

    public boolean isAimReady() {
        final double desiredHeading = this.getDesiredHeading();
        final double currentHeading = this.follower.getTotalHeading();
        return Math.abs(desiredHeading - currentHeading) < MAX_AIM_ERROR;
    }
}
