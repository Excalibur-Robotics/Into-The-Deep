package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class HardwareMap {
    public DcMotor   LFront     = null;
    public DcMotor   RFront     = null;
    public DcMotor   LBack      = null;
    public DcMotor   RBack      = null;
    public DcMotorEx LSlide     = null;
    public DcMotorEx RSlide     = null;
    public Servo     LExtendo   = null;
    public Servo     RExtendo   = null;
    public Servo     Mouth      = null;
    public Servo     Neck       = null;
    public Servo     Claw       = null;
    public Servo     ClawRotate = null;

    com.qualcomm.robotcore.hardware.HardwareMap TeleOpMap  = null;

    public HardwareMap() {
    }

    public void init(com.qualcomm.robotcore.hardware.HardwareMap ahwMap) {
        TeleOpMap = ahwMap;

        LFront     = TeleOpMap.get(DcMotor.class,        "FrontLeft");
        RFront     = TeleOpMap.get(DcMotor.class,        "FrontRight");
        LBack      = TeleOpMap.get(DcMotor.class,          "BackLeft");
        RBack      = TeleOpMap.get(DcMotor.class,         "BackRight");
        LSlide     = TeleOpMap.get(DcMotorEx.class,       "LeftSlide");
        RSlide     = TeleOpMap.get(DcMotorEx.class,      "RightSlide");
        LExtendo   = TeleOpMap.get(Servo.class,         "LeftExtendo");
        RExtendo   = TeleOpMap.get(Servo.class,        "RightExtendo");
        Mouth      = TeleOpMap.get(Servo.class,               "Mouth");
        Neck       = TeleOpMap.get(Servo.class,                "Neck");
        Claw       = TeleOpMap.get(Servo.class,                "Claw");
        ClawRotate = TeleOpMap.get(Servo.class,          "ClawRotate");



        LFront.setDirection(DcMotor.Direction.REVERSE);
        LBack.setDirection(DcMotor.Direction.REVERSE);

        LFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        RExtendo.setDirection(Servo.Direction.REVERSE);


        // Allows slides to be set in a position
        //TODO: Replace this section/method of slide positioning with a PID Controller
        LSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LSlide.setTargetPositionTolerance(50);
        RSlide.setTargetPositionTolerance(50);    }
}