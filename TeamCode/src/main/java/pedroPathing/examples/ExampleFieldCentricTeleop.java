package pedroPathing.examples;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareMap;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

/**
 * This is an example teleop that showcases movement and field-centric driving.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 12/30/2024
 */

@TeleOp(name = "Example Field-Centric Teleop", group = "Examples")
public class ExampleFieldCentricTeleop extends OpMode {
    private Follower follower;
    private final Pose startPose = new Pose(0,0,0);

    HardwareMap robot = new HardwareMap();
    // set the macro movements
    public void Extendo() {
        // Close the claw
        robot.Claw.setPosition(0.4);
        // rotate to up position
        robot.ClawRotate.setPosition(1);
        // extend the slides
        robot.LExtendo.setPosition(.25);
        robot.RExtendo.setPosition(.25);
        // rotate to down position
        robot.ClawRotate.setPosition(0.35);
    }
    public void Retracto() {
        // Close the claw
        robot.Claw.setPosition(0.4);
        // rotate to up position
        robot.ClawRotate.setPosition(1);
        robot.Mouth.setPosition(.5);
        // retract the slides
        robot.LExtendo.setPosition(-1);
        robot.RExtendo.setPosition(-1);
    }

    public void align() {
        // Close the claw
        robot.Claw.setPosition(0.4);
        // rotate to up position
        robot.ClawRotate.setPosition(0.75);
        // retract the slides
        robot.LExtendo.setPosition(-.95);
        robot.RExtendo.setPosition(-.95);
    }

    public void Scoring() {
        // Collection Postition
        robot.Neck.setPosition(0.96);
        // Open Bucket
        //sleep(250);
        //robot.BucketLid.setPosition(.75);
    }
    public void Collection() {
        // Scoring Position
        robot.Neck.setPosition(0.45);
        // Close Bucket
    }

    /** This method is call once when init is played, it initializes the follower **/
    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
    }

    /** This method is called continuously after Init while waiting to be started. **/
    @Override
    public void init_loop() {
    }

    /** This method is called once at the start of the OpMode. **/
    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    /** This is the main loop of the opmode and runs continuously after play **/
    @Override
    public void loop() {
        double slidesDefaultPower = 1;
        int slideHeight = 0;

        /* Update Pedro to move the robot based on:
        - Forward/Backward Movement: -gamepad1.left_stick_y
        - Left/Right Movement: -gamepad1.left_stick_x
        - Turn Left/Right Movement: -gamepad1.right_stick_x
        - Robot-Centric Mode: false
        */

        /** replaced -gamepad1.right_stick_x with -(gamepad1.right_trigger - gamepad1.left_trigger) to enable trigger turning*/
        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -(gamepad1.right_trigger - gamepad1.left_trigger), false);
        follower.update();


        if (gamepad2.dpad_up) {
            // Extendo
            Extendo();
        }
        if (gamepad2.dpad_down) {
            // Retracto
            Retracto();
        }
        if(gamepad2.ps) {
            align();

        }

        // TODO: Replace this section/method of slide positioning with a PID Controller
        if (robot.RSlide.getCurrentPosition() > 10 && robot.LSlide.getCurrentPosition() > 10) {
            robot.RSlide.setPower(slidesDefaultPower);
            robot.LSlide.setPower(slidesDefaultPower);
        }

        if(gamepad1.y) {
            //int heightIncreaser = 100;
            if (slideHeight < 3150) {
                slideHeight += 25;
            }
        }
        if(gamepad1.a){
            if (slideHeight > 30) {
                slideHeight -= 25;
            }
        }

        if (gamepad2.right_trigger > 0.5) robot.Claw.setPosition(0.4);


        if (gamepad2.left_trigger > 0.5) {
            // Open claw
            robot.Claw.setPosition(0);
        }
        if (gamepad2.x) {
            // Close bucket
            robot.Mouth.setPosition(1);
        }
        if (gamepad2.b) {
            // Open bucket
            robot.Mouth.setPosition(.7);
        }
        if(gamepad2.left_bumper){
            Scoring();
        }
        if(gamepad2.right_bumper){
            Collection();
        }

        // TODO: Replace this section/method of slide positioning with a PID Controller
        // Set the slides to the target position
        if ((Math.abs(robot.LSlide.getCurrentPosition() - slideHeight) > 50 && Math.abs(robot.RSlide.getCurrentPosition() - slideHeight) > 50) || (robot.LSlide.getCurrentPosition() < -100 && robot.RSlide.getCurrentPosition() > 100)) {
            robot.LSlide.setTargetPosition(-slideHeight);
            robot.RSlide.setTargetPosition(slideHeight);
            robot.LSlide.setPower(-1);
            robot.RSlide.setPower(1);
        } else {
            robot.LSlide.setPower(0);
            robot.RSlide.setPower(0);
        }




        /* Telemetry Outputs of our Follower */
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));

        /* Update Telemetry to the Driver Hub */
        telemetry.update();

    }

    /** We do not use this because everything automatically should disable **/
    @Override
    public void stop() {
    }
}