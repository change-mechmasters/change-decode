package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;

// SAFETY: The data here resets to the initial values below between power-cycles.
//  It can be changed by any OpMode and it'll persist globally (between power-cycles) until restart.
public class BotContext {
    public static Alliance alliance = Alliance.BLUE;
    // SAFETY: This pose shouldn't be used. It is only here for testing if running teleop directly;
    //  The Auto should be ran to reset the pose.
    public static Pose botPose = new Pose(72, 72);

    public enum Alliance {
        BLUE,
        RED,
    }

    public static void setGamepadColor(Gamepad gamepad) {
        if (BotContext.alliance == Alliance.BLUE)
            gamepad.setLedColor(0, 0, 1, Gamepad.LED_DURATION_CONTINUOUS);
        else
            gamepad.setLedColor(1, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);
    }
}
