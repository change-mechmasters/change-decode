package org.firstinspires.ftc.teamcode;

// SAFETY: The data here resets to the initial values below between power-cycles.
//  It can be changed by any OpMode and it'll persist globally (between power-cycles) until restart.
public class BotContext {
    public static Alliance alliance = Alliance.BLUE;

    public enum Alliance {
        BLUE,
        RED,
    }
}
