package org.firstinspires.ftc.teamcode;

import com.pedropathing.control.PIDFController;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class Robot {
    public State state;
    public Follower follower;
    public int artifactCount;
    public boolean autoAim;
    private double desiredHeading;
    private ShooterState shooterState;
    private final PIDFController aimController;
    private final ElapsedTime emptyShooterTimer = new ElapsedTime();
    private final Hubs hubs;
    private final Intake intake;
    private final ArtifactSensors artifactSensors;
    private final Outtake outtake;
    private final Shooter shooter;

    private static final double FIELD_DISTANCE_CONVERSION_RATE = 3.6 / 144.0; // Competition manual
    private static final double MAX_AIM_ERROR = Math.toRadians(7.5);
    private static final double MAX_EMPTY_TIME = 1.75;
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

    public Robot(HardwareMap hw, Pose initialPose) {
        this.hubs = new Hubs(hw);
        this.intake = new Intake(hw);
        this.artifactSensors = new ArtifactSensors(hw);
        this.outtake = new Outtake(hw);
        this.shooter = new Shooter(hw);

        this.follower = Constants.createFollower(hw);
        this.follower.setStartingPose(initialPose);
        follower.update();

        this.state = State.DRIVING;
        this.autoAim = false;
        this.aimController = new PIDFController(follower.constants.coefficientsHeadingPIDF);

        if (BotContext.alliance == BotContext.Alliance.BLUE)
            GOAL = BLUE_GOAL;
        else
            GOAL = RED_GOAL;
    }

    public void update() {
        hubs.update();
        follower.update();
        this.artifactCount = this.artifactSensors.getArtifactCount();
        BotContext.botPose = this.follower.getPose();
        if (this.autoAim) {
            this.desiredHeading = this.getDesiredHeading();
            this.aimController.updateError(this.getHeadingError());
        }

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

    public void setTeleOpDrive(Gamepad gamepad) {
        if (this.autoAim)
            this.follower.setTeleOpDrive(
                    -gamepad.left_stick_y, -gamepad.left_stick_x, this.aimController.run()
            );
        else
            this.follower.setTeleOpDrive(
                    -gamepad.left_stick_y, -gamepad.left_stick_x, -gamepad.right_stick_x
            );
    }

    public void toggleAutoAim() {
        this.autoAim = !this.autoAim;
    }

    // SAFETY: It is expected that these state transition functions
    //  will be called in the right state, otherwise the correct state transitions
    //  may not get triggered and everything will come to ruin.
    public void enterShootingState() {
        this.state = State.SHOOTING;
        this.shooterState = ShooterState.PREPARING;
        this.autoAim = true;
    }

    public void exitShootingState() {
        this.state = State.DRIVING;
        this.outtake.setDown();
        this.autoAim = false;
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
        // STRATEGY: Intake wastes too much power in driving state.
        this.intake.setEnabled(false);
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
        this.intake.setEnabled(true);
        switch (this.shooterState) {
            case PREPARING:
                // STRATEGY: Shooter acceleration is not the bottleneck; the intake is.
                //  Hence, incremental distance changes are not necessary.
                this.shooter.setDesiredVelocity(this.getDistanceToGoal());
                this.outtake.setDown();
                if (this.shooter.isReady() && this.artifactSensors.isShooterArtifactReady())
                    this.shooterState = ShooterState.SHOOTING;
                break;
            case SHOOTING:
                this.outtake.setUp();
                if (!this.artifactSensors.isShooterArtifactReady())
                    this.shooterState = ShooterState.PREPARING;
                break;
        }

        if (this.artifactCount == 0) {
            if (this.emptyShooterTimer.seconds() > MAX_EMPTY_TIME)
                this.exitShootingState();
        }
        else
            this.emptyShooterTimer.reset();
    }

    private void handleIntakingState() {
        // TODO: Automated intaking state. This requires:
        //  - [x] Upgrading ArtifactSensor into ArtifactSensors, allowing for
        //    knowing how many artifacts are in storage at any moment.
        //  - [ ] Adding automatic camera-detection of artifacts, allowing for
        //    the bot to automatically charge to them.
        this.intake.setEnabled(true);
        this.shooter.setEnabled(false);
        if (this.artifactCount == 3)
            this.exitIntakingState();
    }

    private double getDistanceToGoal() {
        final Pose botPose = this.follower.getPose();
        final double x_diff = GOAL.getX() - botPose.getX();
        final double y_diff = GOAL.getY() - botPose.getY();
        final double distance_ticks = Math.sqrt(x_diff*x_diff + y_diff*y_diff);
        return distance_ticks * FIELD_DISTANCE_CONVERSION_RATE;
    }

    private double getDesiredHeading() {
        final Pose botPose = this.follower.getPose();
        final double opp = Math.abs(GOAL.getY() - botPose.getY());
        final double adj = Math.abs(GOAL.getX() - botPose.getX());
        final double angle = Math.atan(opp / adj);
        if (botPose.getX() > GOAL.getX())
            return Math.toRadians(180) - angle;
        else
            return angle;
    }

    private double getHeadingError() {
        final double currHeading = this.follower.getHeading();
        final double direction = MathFunctions.getTurnDirection(currHeading, this.desiredHeading);
        return direction * MathFunctions.getSmallestAngleDifference(currHeading, this.desiredHeading);
    }
}
