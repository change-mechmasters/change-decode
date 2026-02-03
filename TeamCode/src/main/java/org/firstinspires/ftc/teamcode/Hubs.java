package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class Hubs {
    private final List<LynxModule> hubs;

    public Hubs(HardwareMap hw) {
        this.hubs = hw.getAll(LynxModule.class);
        for (LynxModule hub : this.hubs)
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
    }

    // SAFETY: If this class has been initialized, then
    //  this MUST be ran once (and only once!) at the start(!) of every cycle.
    public void update() {
        for (LynxModule hub : this.hubs)
            hub.clearBulkCache();
    }
}
