package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Optional;

public class Shooter {
    public boolean isEnabled;
    public double angularVelocity;
    public final DcMotorEx motor;
    // TODO: Tune PIDF
    private static final PIDFCoefficients PIDF = new PIDFCoefficients(30, 0, 0.01, 0);
    private static final double TARGET_HEIGHT = 0.9845; // DECODE Competition Manual, p. 64
    private static final double BOT_HEIGHT = 0.36;
    private static final double THETA = 70;
    private static final double STANDARD_GRAVITY = 9.80665;
    private static final double WHEEL_RADIUS = 0.045;
    private static final double GEAR_RATIO = 21.0 / 26.0;
    // TODO: Tune
    private static final double ENERGY_TRANSFER_PROPORTION = 1;
    private static final double MAX_VELOCITY = 1000;

    public Shooter(HardwareMap hw) {
        this.motor = hw.get(DcMotorEx.class, "shooter");
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, PIDF);
        this.setEnabled(false);
    }

    public boolean setDesiredVelocity(double distance) {
        final Optional<Double> artifactVelocityMaybe = this.calculateDesiredVelocity(distance);
        if (!artifactVelocityMaybe.isPresent())
            return false;

        final double artifactVelocity = artifactVelocityMaybe.get();
        final double angularVelocity = this.calculateAngularVelocity(artifactVelocity);

        this.setAngularVelocity(angularVelocity);
        return true;
    }

    public boolean isReady() {
        return this.motor.getVelocity(AngleUnit.RADIANS) == this.angularVelocity;
    }
    
    public void setEnabled(boolean enabled) {
        if (enabled)
            this.motor.setPower(1);
        else
            this.motor.setPower(0);
        this.isEnabled = enabled;
        this.setAngularVelocity(this.angularVelocity);
    }

    private void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (this.isEnabled)
            this.motor.setVelocity(angularVelocity, AngleUnit.RADIANS);
        else
            this.motor.setVelocity(0.0, AngleUnit.RADIANS);
    }

    private double calculateAngularVelocity(double artifactVelocity) {
        final double k = Math.sqrt(ENERGY_TRANSFER_PROPORTION);
        final double angularVelocity = artifactVelocity / (k * WHEEL_RADIUS);
        return angularVelocity / GEAR_RATIO;
    }

    private Optional<Double> calculateDesiredVelocity(double distance) {
        final double heightDiff = TARGET_HEIGHT - BOT_HEIGHT;
        final double cos = Math.cos(THETA);
        final double tan = Math.tan(THETA);

        final double denominator = (2*cos*cos)*(distance*tan - heightDiff);
        if (denominator <= 0)
            return Optional.empty();

        final double inner = (distance*distance*STANDARD_GRAVITY) / denominator;
        final double velocity = Math.sqrt(inner);
        if (velocity > MAX_VELOCITY)
            return Optional.empty();

        return Optional.of(velocity);
    }
}
