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

@Autonomous(name = "Danny Auto", group = "Autos")
public class BlueSample extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    public DcMotorEx LSlide     = null;
    public DcMotorEx RSlide     = null;
    public Servo     LExtendo   = null;
    public Servo     RExtendo   = null;
    public Servo     Mouth      = null;
    public Servo     Neck       = null;
    public Servo     Claw       = null;
    public Servo     ClawRotate = null;


    private final Pose startPose = new Pose(1, 113, Math.toRadians(0));
    private final Pose parkPose = new Pose(70, 90, Math.toRadians(90));

    /** Scoring Pose of our  It is facing the submersible at a -45 degree (315 degree) angle. */
    private final Pose scorePose = new Pose(8.5, 132.00, Math.toRadians(315));

    /** Lowest (First) Sample from the Spike Mark */
    private final Pose pickup1Pose = new Pose(21.50, 122.50, Math.toRadians(0));

    /** Middle (Second) Sample from the Spike Mark */
    private final Pose pickup2Pose = new Pose(39.108, 131.827, Math.toRadians(0));



    /** These are our Paths and PathChains that we will define in buildPaths() */
    private PathChain park;
    private PathChain scorePreload, grabPickup1, grabPickup2, scorePickup1, scorePickup2, scorePickup3;

    public void slides(String dir, int height) {

        int slideHeight = height;
        telemetry.addData("method", "method");

        RSlide.setTargetPosition(height);
        telemetry.addData("rightslidepos", RSlide.getCurrentPosition());

        LSlide.setTargetPosition(-height);
        telemetry.addData("leftslidepos", LSlide.getCurrentPosition());


        if (dir == "up") {
            while (Math.abs(LSlide.getCurrentPosition() - slideHeight) > 10 && Math.abs(RSlide.getCurrentPosition() - slideHeight) > 10) {
                LSlide.setPower((-0.6));
                RSlide.setPower((0.6));
            }


            LSlide.setPower(-0.05);
            RSlide.setPower(0.05);
        } else if (dir == "down") {
            while (Math.abs(LSlide.getCurrentPosition() - slideHeight) > 10 && Math.abs(RSlide.getCurrentPosition() - slideHeight) > 10) {
                LSlide.setPower((0.6));
                RSlide.setPower((-0.6));
            }


            LSlide.setPower(-0.05);
            RSlide.setPower(0.05);

        }
    }
        public void Extendo() {
            // Close the claw
            Claw.setPosition(0.4);
            // rotate to up position
            ClawRotate.setPosition(1);
            // extend the slides
            LExtendo.setPosition(.25);
            RExtendo.setPosition(.25);
            // rotate to down position
            ClawRotate.setPosition(0.35);
        }
        public void Retracto() {
            // Close the claw
            Claw.setPosition(0.4);
            // rotate to up position
            ClawRotate.setPosition(1);
            Mouth.setPosition(.5);
            // retract the slides
            LExtendo.setPosition(-1);
            RExtendo.setPosition(-1);
        }

        public void align() {
            // Close the claw
            Claw.setPosition(0.4);
            // rotate to up position
            ClawRotate.setPosition(0.75);
            // retract the slides
            LExtendo.setPosition(-.95);
            RExtendo.setPosition(-.95);
            //move the neck
            Neck.setPosition(.45);
        }

        public void Scoring() {
            // Collection Postition
            Neck.setPosition(0.96);
            // Open Bucket
            //sleep(250);
            //BucketLid.setPosition(.75);
        }
        public void Collection() {
            // Scoring Position
            Neck.setPosition(0.45);
            // Close Bucket
        }



    /** Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts. **/
    public void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();
        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierCurve(
                        new Point(scorePose),
                        new Point(10.899, 121.950),
                        new Point(pickup1Pose)
                        ))
                .setTangentHeadingInterpolation()
                .build();
        park = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(parkPose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading())
                .build();

    }

    public void scoreArm(){

    }

    public void autonomousPathUpdate() {
            switch (pathState){
                case 0:
                    follower.followPath(scorePreload);
                    setPathState(4);
                    break;
                case 4:
                    if(!follower.isBusy()) {
                        slides("up", 3150);
                        Scoring();
                        Mouth.setPosition(.7);

                        if(pathTimer.getElapsedTimeSeconds()>=6) {
                            setPathState(1);
                        }
                        // follower
                    }
                case 1:
                    if(!follower.isBusy()) {




                        follower.followPath(grabPickup1);
                        setPathState(2);
                        break;
                    }
                case 2:
                    if(!follower.isBusy()){


                        follower.followPath(park);
                        setPathState(3);
                        break;
                    }
                case 3:
                    if(!follower.isBusy()){
                        slides("up", 1150);

                        setPathState(-1);
                        break;
                    }
            }

        }

    /** This change the states of the paths and actions
     * It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {
        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
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

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();


        LSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide"); // This is motor port 0
        RSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");

        Mouth = hardwareMap.get(Servo.class, "Mouth");
        Claw = hardwareMap.get(Servo.class, "Claw");
        Neck = hardwareMap.get(Servo.class, "Neck");
        ClawRotate = hardwareMap.get(Servo.class, "ClawRotate");

        setPathState(0);
    }


}

