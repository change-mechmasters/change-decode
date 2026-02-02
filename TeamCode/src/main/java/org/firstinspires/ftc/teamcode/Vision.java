// This class is incomplete & unused
package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.HashMap;

public class Vision {
    private final VisionPortal visionPortal;
    private final AprilTagProcessor aprilTagProcessor;
    private final AprilTagLibrary aprilTagLibrary;
    private final HashMap<Integer, Pose> aprilTagPoses;

    public Vision(HardwareMap hw) {
        WebcamName webcamName = hw.get(WebcamName.class, "webcam");

        // https://ftc-resources.firstinspires.org/ftc/archive/2026/field/apriltag-a4
        final double tagSize = 16.5;
        this.aprilTagLibrary = new AprilTagLibrary.Builder()
                .addTag(21, "GPP", tagSize, DistanceUnit.CM)
                .addTag(22, "PGP", tagSize, DistanceUnit.CM)
                .addTag(23, "PPG", tagSize, DistanceUnit.CM)
                .addTag(20, "BLUE", tagSize, DistanceUnit.CM)
                .addTag(24, "RED", tagSize, DistanceUnit.CM)
                .build();

        this.aprilTagPoses = new HashMap<>();
        this.aprilTagPoses.put(20, new Pose(0, 0, 0));
        this.aprilTagPoses.put(24, new Pose(0, 0, 0));

        this.aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(this.aprilTagLibrary)
                .setOutputUnits(DistanceUnit.CM, AngleUnit.RADIANS)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .build();

        // STRATEGY: We only use AprilTags for localization, so we prefer range over rate.
        this.aprilTagProcessor.setDecimation(1);

        this.visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .addProcessor(this.aprilTagProcessor)
                .enableLiveView(true)
                .setShowStatsOverlay(true)
                .build();
    }
}
