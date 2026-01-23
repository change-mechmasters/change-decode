package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public boolean isEnabled;
    private final DcMotorEx intake1;
    private final DcMotorEx intake2;

    public Intake(HardwareMap hw) {
        this.intake1 = hw.get(DcMotorEx.class, "intake1-parallelLeft");
        this.intake1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.intake2 = hw.get(DcMotorEx.class, "intake2-parallelRight");
        this.intake2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.intake2.setDirection(DcMotorSimple.Direction.REVERSE);
        this.isEnabled = false;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (enabled) {
            this.intake1.setPower(1);
            this.intake2.setPower(1);
        }
        else {
            this.intake1.setPower(0);
            this.intake2.setPower(0);
        }
    }
}
