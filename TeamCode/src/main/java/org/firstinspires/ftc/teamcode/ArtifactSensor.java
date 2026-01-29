package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ArtifactSensor {
    public final ColorRangeSensor sensor;
    private static final double MIN_HUE = 400;

    public ArtifactSensor(HardwareMap hw) {
        this.sensor = hw.get(ColorRangeSensor.class, "colorSensor");
        this.sensor.enableLed(true);
    }

    public boolean isArtifactPresent() {
        return MIN_HUE < this.getHue();
    }

    private double getHue() {
        return this.sensor.red() + this.sensor.green() + this.sensor.blue();
    }
}
