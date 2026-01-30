package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ArtifactSensors;

@TeleOp
public class SensorTest extends OpMode {
    private ArtifactSensors artifactSensors;

    @Override
    public void init() {
        this.artifactSensors = new ArtifactSensors(hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addData("Shooter artifact", this.artifactSensors.isShooterArtifactReady());
        telemetry.addData("Artifact count", this.artifactSensors.getArtifactCount());
    }
}
