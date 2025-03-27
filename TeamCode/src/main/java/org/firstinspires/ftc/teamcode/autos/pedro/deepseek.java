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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "d", group = "Autos")
public class deepseek extends OpMode {
    private Follower follower;
    private Timer pathTimer, opmodeTimer;
    private int pathState;

    // State Constants
    private static final int
            STATE_MOVE_FORWARD = 0,
            STATE_RAISE_SLIDES = 1,
            STATE_LOWER_SLIDES = 2,
            STATE_MOVE_BACKWARD = 3;


    // Hardware
    public DcMotorEx LSlide, RSlide;
    public Servo LExtendo, RExtendo, Mouth, Neck, Claw, ClawRotate;

    // Positions
    private final Pose startPose = new Pose(1, 113, Math.toRadians(0));
    private final Pose parkPose = new Pose(70, 90, Math.toRadians(90));
    private final Pose scorePose = new Pose(8.5, 132.00, Math.toRadians(315));

    // Paths
    private PathChain moveForward, moveBackward, park, scorePreload;

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();

        // Initialize hardware
        LSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        RSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");
        Mouth = hardwareMap.get(Servo.class, "Mouth");
        Claw = hardwareMap.get(Servo.class, "Claw");
        Neck = hardwareMap.get(Servo.class, "Neck");
        ClawRotate = hardwareMap.get(Servo.class, "ClawRotate");

        // Configure motors
        LSlide.setDirection(DcMotor.Direction.REVERSE);
        RSlide.setDirection(DcMotor.Direction.FORWARD);
        LSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        setPathState(STATE_MOVE_FORWARD);
    }

    public void buildPaths() {
        // Forward path (start to score position)
        moveForward = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();

        // Backward path (score position to start)
        moveBackward = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(startPose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), startPose.getHeading())
                .build();
    }

    public void slides(String dir, int height) {
        RSlide.setTargetPosition(height);
        LSlide.setTargetPosition(-height);

        RSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (dir.equals("up")) {
            RSlide.setPower(0.6);
            LSlide.setPower(-0.6);
        } else if (dir.equals("down")) {
            RSlide.setPower(-0.6);
            LSlide.setPower(0.6);
        }
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case STATE_MOVE_FORWARD:
                follower.followPath(moveForward);
                setPathState(STATE_RAISE_SLIDES);
                break;

            case STATE_RAISE_SLIDES:
                if (!follower.isBusy()) {
                    slides("up", 3150); // Raise slides
                    setPathState(STATE_LOWER_SLIDES);
                }
                break;

            case STATE_LOWER_SLIDES:
                if (!LSlide.isBusy() && !RSlide.isBusy()) {
                    slides("down", 0); // Lower slides
                    setPathState(STATE_MOVE_BACKWARD);
                }
                break;

            case STATE_MOVE_BACKWARD:
                if (!LSlide.isBusy() && !RSlide.isBusy()) {
                    follower.followPath(moveBackward);
                    setPathState(-1); // End sequence
                }
                break;
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

        // Telemetry
        telemetry.addData("State", pathState);
        telemetry.addData("Left Slide Pos", LSlide.getCurrentPosition());
        telemetry.addData("Right Slide Pos", RSlide.getCurrentPosition());
        telemetry.addData("X Position", follower.getPose().getX());
        telemetry.addData("Y Position", follower.getPose().getY());
        telemetry.update();
    }
}