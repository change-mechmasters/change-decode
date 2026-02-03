package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Optional;

public class Shooter {
    public boolean isEnabled;
    private double angularVelocity;
    private final DcMotorEx motor;
    private static final double TARGET_HEIGHT = 0.9845; // DECODE Competition Manual, p. 64
    private static final double BOT_HEIGHT = 0.36;
    private static final double LAUNCH_ANGLE = Math.toRadians(60);
    private static final double STANDARD_GRAVITY = 9.80665;
    private static final PIDFCoefficients PIDF = new PIDFCoefficients(300, 0, 0, 20);
    private static final double CONVERSION_RATE = 2.32;
    private static final double CONVERSION_ADJUSTMENT = 1.12;
    // STRATEGY: The maximum angular velocity is 18-21 depending on the battery's voltage,
    //  so the maximum velocity will be 7.28-8.57. We choose the pessimistic case here,
    //  so that we'll be sure that all shots will land regardless of voltage.
    //  We lose some distance but it's okay because we can already shoot from anywhere with this.
    private static final double MAX_VELOCITY = 7.28;
    private static final double MAX_ERROR = 0.5;

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
        final double error = this.angularVelocity - this.motor.getVelocity(AngleUnit.RADIANS);
        return Math.abs(error) < MAX_ERROR;
    }
    
    public void setEnabled(boolean enabled) {
        if (this.isEnabled == enabled)
            return;

        this.isEnabled = enabled;
        this.setAngularVelocity(this.angularVelocity);
        if (enabled)
            this.motor.setPower(1);
        else
            this.motor.setPower(0);
    }

    private void setAngularVelocity(double angularVelocity) {
        final double error = Math.abs(this.angularVelocity - angularVelocity);
        if (error < MAX_ERROR)
            return;

        this.angularVelocity = angularVelocity;
        if (this.isEnabled)
            this.motor.setVelocity(angularVelocity, AngleUnit.RADIANS);
        else
            this.motor.setVelocity(0.0, AngleUnit.RADIANS);
    }

    private double calculateAngularVelocity(double artifactVelocity) {
        return CONVERSION_RATE*artifactVelocity + CONVERSION_ADJUSTMENT;
    }

    private Optional<Double> calculateDesiredVelocity(double distance) {
        final double heightDiff = TARGET_HEIGHT - BOT_HEIGHT;
        final double cos = Math.cos(LAUNCH_ANGLE);
        final double tan = Math.tan(LAUNCH_ANGLE);

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
