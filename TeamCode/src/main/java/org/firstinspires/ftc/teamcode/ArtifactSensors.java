package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ArtifactSensors {
    private final ArtifactSensor sensor1;
    private final ArtifactSensor sensor2;
    private final ArtifactSensor sensor3;

    public ArtifactSensors(HardwareMap hw) {
        this.sensor1 = new ArtifactSensor(hw, "colorSensor1");
        this.sensor2 = new ArtifactSensor(hw, "colorSensor2");
        this.sensor3 = new ArtifactSensor(hw, "colorSensor3");
    }

    public boolean isShooterArtifactReady() {
        return this.sensor1.isArtifactPresent();
    }

    public int getArtifactCount() {
        int s1 = this.sensor1.isArtifactPresent() ? 1 : 0;
        int s2 = this.sensor2.isArtifactPresent() ? 1 : 0;
        int s3 = this.sensor3.isArtifactPresent() ? 1 : 0;
        return s1 + s2 + s3;
    }
}

class ArtifactSensor {
    private boolean artifactPresent;
    private final ColorSensor sensor;
    private final ElapsedTime sensorTimer = new ElapsedTime();
    private static final double MIN_INTENSITY = 450;
    private static final double MAX_CACHE_TIME = 50;

    public ArtifactSensor(HardwareMap hw, String name) {
        this.sensor = hw.get(ColorSensor.class, name);
        this.sensor.enableLed(true);
        this.artifactPresent = this.checkPresent();
        this.sensorTimer.reset();
    }

    public boolean isArtifactPresent() {
        if (sensorTimer.milliseconds() > MAX_CACHE_TIME) {
            this.artifactPresent = this.checkPresent();
            this.sensorTimer.reset();
        }
        return this.artifactPresent;
    }

    private boolean checkPresent() {
        final double intensity = this.sensor.red() + this.sensor.green() + this.sensor.blue();
        return intensity > MIN_INTENSITY;
    }
}