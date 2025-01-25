package org.firstinspires.ftc.teamcode.autos.pedro;

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

import org.firstinspires.ftc.teamcode.HardwareMap;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Danny Auto", group = "Autos")
public class BlueSample extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;


    private final Pose startPose = new Pose(1, 113, Math.toRadians(0));
    private final Pose parkPose = new Pose(70, 90, Math.toRadians(90));

    /** Scoring Pose of our robot. It is facing the submersible at a -45 degree (315 degree) angle. */
    private final Pose scorePose = new Pose(9.40, 132.00, Math.toRadians(315));

    /** Lowest (First) Sample from the Spike Mark */
    private final Pose pickup1Pose = new Pose(21.50, 122.50, Math.toRadians(0));

    /** Middle (Second) Sample from the Spike Mark */
    private final Pose pickup2Pose = new Pose(39.108, 131.827, Math.toRadians(0));



    /** These are our Paths and PathChains that we will define in buildPaths() */
    private PathChain park;
    private PathChain scorePreload, grabPickup1, grabPickup2, scorePickup1, scorePickup2, scorePickup3;

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
    public void autonomousPathUpdate() {
            switch (pathState){
                case 0:
                    follower.followPath(scorePreload);
                    setPathState(1);
                    break;
                case 1:
                    if(!follower.isBusy()) {



                        follower.followPath(grabPickup1);
                        setPathState(2);
                        break;
                    }
                case 2:
                    if(!follower.isBusy()){


                        follower.followPath(park);
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
        setPathState(0);
    }


}

