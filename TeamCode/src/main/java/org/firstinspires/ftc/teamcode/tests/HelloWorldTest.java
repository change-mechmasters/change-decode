package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class HelloWorldTest extends OpMode {
    @Override
    public void init() {}

    @Override
    public void start() {
        telemetry.addLine("Hello, World!");
    }

    @Override
    public void loop() {}
}
