package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Robot;

@Autonomous
public class MainAuto extends OpMode {
    private Robot robot;
    private final Pose startPose = new Pose(72, 72);

    @Override
    public void init() {
        this.robot = new Robot(hardwareMap, startPose, 3);
    }

    @Override
    public void loop() {
        this.robot.update();
    }
}
