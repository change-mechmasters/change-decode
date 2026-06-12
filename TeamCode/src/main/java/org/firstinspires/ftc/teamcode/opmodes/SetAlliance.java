package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.BotContext;

@TeleOp
public class SetAlliance extends OpMode {
    @Override
    public void init() {}

    @Override
    public void loop() {
        telemetry.addData("Current alliance", BotContext.alliance);
        telemetry.addLine("Press left bumper for BLUE and right bumper for RED");

        if (gamepad1.leftBumperWasPressed()) 
            BotContext.alliance = BotContext.Alliance.BLUE;
        if (gamepad1.rightBumperWasPressed())
            BotContext.alliance = BotContext.Alliance.RED;
    }
}
