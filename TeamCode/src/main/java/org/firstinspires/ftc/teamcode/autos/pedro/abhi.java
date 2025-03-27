package org.firstinspires.ftc.teamcode.autos.pedro;

import static java.lang.Thread.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.HardwareMap;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Abhi", group = "Autos")
public class abhi extends OpMode {
    private Follower follower;
    private Timer pathTimer;
    private int pathState;

    // Motors and Servos
    private DcMotorEx LSlide, RSlide;
    private Servo Claw, ClawRotate;

    // Define Start and Movement Positions
    private final Pose startPose = new Pose(1, 113, Math.toRadians(0));
    private final Pose forwardPose = new Pose(30, 113, Math.toRadians(0));

    // Paths
    private PathChain moveForward, moveBackward;

    @Override
    public void init() {
        pathTimer = new Timer();
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        // Hardware Mapping
        LSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        RSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");
        Claw = hardwareMap.get(Servo.class, "Claw");
        ClawRotate = hardwareMap.get(Servo.class, "ClawRotate");

        // Build Paths
        buildPaths();
    }

    public void buildPaths() {
        moveForward = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(forwardPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), forwardPose.getHeading())
                .build();

        moveBackward = follower.pathBuilder()
                .addPath(new BezierLine(new Point(forwardPose), new Point(startPose)))
                .setLinearHeadingInterpolation(forwardPose.getHeading(), startPose.getHeading())
                .build();
    }

    @Override
    public void start() {
        setPathState(0);
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Move forward while raising slides
                follower.followPath(moveForward);
                slides("up", 3000);  // Raise slides to position 3000
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy()) {
                    setPathState(2);
                }
                break;

            case 2: // Move backward while lowering slides
                follower.followPath(moveBackward);
                slides("down", 0);  // Lower slides to bottom
                setPathState(3);
                break;

            case 3:
                if (!follower.isBusy()) {
                    setPathState(-1); // End autonomous
                }
                break;
        }
    }

    public void slides(String dir, int height) {
        RSlide.setTargetPosition(height);
        LSlide.setTargetPosition(-height);

        if (dir.equals("up")) {
            LSlide.setPower(-0.6);
            RSlide.setPower(0.6);
        } else if (dir.equals("down")) {
            LSlide.setPower(0.6);
            RSlide.setPower(-0.6);
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.update();
    }
}
