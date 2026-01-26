package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants FOLLOWER_CONSTANTS = new FollowerConstants()
            .mass(8.6)
            .forwardZeroPowerAcceleration(-43.7)
            .lateralZeroPowerAcceleration(-63.7)
            .translationalPIDFCoefficients(new PIDFCoefficients(.08, 0, .006, .02))
            .headingPIDFCoefficients(new PIDFCoefficients(0.6, 0, 0.009, 0.025))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.025, 0, 0.0001, 0.01, 0.6))
            .centripetalScaling(0.005);

    public static MecanumConstants DRIVE_CONSTANTS = new MecanumConstants()
            .maxPower(0.5) // TODO: Fix the drive to allow for strafe at max power
            .rightFrontMotorName("frontRight-perpendicular")
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftFrontMotorName("frontLeft")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorName("backRight")
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorName("backLeft")
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .xVelocity(61)
            .yVelocity(17.75);

    public static ThreeWheelConstants LOCALIZER_CONSTANTS = new ThreeWheelConstants()
            .rightEncoder_HardwareMapName("intake2-parallelRight")
            .rightEncoderDirection(Encoder.REVERSE)
            .rightPodY(-7.87)   // -20cm
            .leftEncoder_HardwareMapName("intake1-parallelLeft")
            .leftEncoderDirection(Encoder.FORWARD)
            .leftPodY(+8.27)    // +21cm
            .strafeEncoder_HardwareMapName("frontRight-perpendicular")
            .strafeEncoderDirection(Encoder.FORWARD)
            .strafePodX(-5.91) // -15cm
            .forwardTicksToInches(.00305)
            .strafeTicksToInches(.00302)
            .turnTicksToInches(.00291);

    public static PathConstraints PATH_CONSTRAINTS = new PathConstraints(
            0.99, 100, 1, 1
    );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(FOLLOWER_CONSTANTS, hardwareMap)
                .mecanumDrivetrain(DRIVE_CONSTANTS)
                .threeWheelLocalizer(LOCALIZER_CONSTANTS)
                .pathConstraints(PATH_CONSTRAINTS)
                .build();
    }
}
