package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ArtifactSensor;

@Disabled
@TeleOp
public class SensorTest extends OpMode {
    private ArtifactSensor artifactSensor;

    @Override
    public void init() {
        this.artifactSensor = new ArtifactSensor(hardwareMap);
    }

    @Override
    public void loop() {
        boolean isArtifactPresent = this.artifactSensor.isArtifactPresent();
        telemetry.addData("Is an artifact present?", isArtifactPresent);
    }
}
