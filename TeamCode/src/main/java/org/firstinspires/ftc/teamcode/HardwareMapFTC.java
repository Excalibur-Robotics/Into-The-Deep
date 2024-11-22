package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HardwareMapFTC {
    public DcMotor LFront = null;
    public DcMotor RFront = null;
    public DcMotor LBack = null;
    public DcMotor RBack = null;
    public DcMotorEx LSlide = null;
    public DcMotorEx RSlide = null;

    HardwareMap TeleOpMap = null;

    public HardwareMapFTC() {
    }

    public void init(HardwareMap ahwMap) {
        TeleOpMap = ahwMap;

        LFront = TeleOpMap.get(DcMotor.class, "BackLeft");
        RFront = TeleOpMap.get(DcMotor.class, "BackRight");
        LBack  = TeleOpMap.get(DcMotor.class, "BackLeft");
        RBack  = TeleOpMap.get(DcMotor.class, "BackRight");
        LSlide = TeleOpMap.get(DcMotorEx.class, "LeftSlide");
        RSlide = TeleOpMap.get(DcMotorEx.class, "RightSlide");
    }
}