package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ArtifactSensor {
    private final ColorRangeSensor sensor;
    private static final double MAX_DISTANCE = 5;

    public ArtifactSensor(HardwareMap hw) {
        this.sensor = hw.get(ColorRangeSensor.class, "colorSensor");
        this.sensor.enableLed(true);
    }

    public boolean isArtifactPresent() {
        return this.sensor.getDistance(DistanceUnit.CM) < MAX_DISTANCE;
    }
}
