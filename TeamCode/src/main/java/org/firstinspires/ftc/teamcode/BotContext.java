package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;

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
}
