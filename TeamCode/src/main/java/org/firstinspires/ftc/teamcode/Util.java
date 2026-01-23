package org.firstinspires.ftc.teamcode;

public class Util {
    public static final int POLL_TIME = 5;

    // SAFETY: The only running thread is under our control and never calls `Thread.interrupt()`.
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException("Encountered an unexpected interrupt: " + e);
        }
    }
}
