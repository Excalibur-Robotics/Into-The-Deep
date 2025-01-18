package org.firstinspires.ftc.teamcode.autos.encoder;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareMap;


@Autonomous(name="blueRight", group="Robot")
public class blueRight extends LinearOpMode {


    static final double FORWARD_SPEED = 0.7;
    static final double TURN_SPEED = 0.6;


    HardwareMap robot = new HardwareMap();


    int height = 0;


    public void turn(String dir, int time) {


        if (dir.equals("L")) {
            robot.RFront.setPower(TURN_SPEED);
            robot.RBack.setPower(TURN_SPEED);
            robot.LFront.setPower(-TURN_SPEED);
            robot.LBack.setPower(-TURN_SPEED);
            sleep(time);
        } else if (dir.equals("R")) {
            robot.LFront.setPower(TURN_SPEED);
            robot.LBack.setPower(TURN_SPEED);
            robot.RFront.setPower(-TURN_SPEED);
            robot.RBack.setPower(-TURN_SPEED);
            sleep(time);
        }
        //sleep(time);
        stopMotors();
    }


    public void strafe(String dir, int left, int right) {
        telemetry.addData("strafe", "strafing");
        robot.LBack.setTargetPosition(left);
        robot.RBack.setTargetPosition(right);

        robot.LFront.setTargetPosition(left);
        robot.RFront.setTargetPosition(right);

        if (dir.equals("L")) {
            telemetry.addData("left", "lefting");
            while (Math.abs(robot.RBack.getCurrentPosition() - right) > 15 && Math.abs(robot.LBack.getCurrentPosition() - left) > 15) {
                robot.LFront.setPower(-FORWARD_SPEED);
                robot.LBack.setPower(FORWARD_SPEED);
                robot.RFront.setPower(-FORWARD_SPEED);
                robot.RBack.setPower(FORWARD_SPEED);
            }
        } else if (dir.equals("R")) {
            telemetry.addData("right", "righting");
            while (Math.abs(robot.RBack.getCurrentPosition() - right) > 15 && Math.abs(robot.LBack.getCurrentPosition() - left) > 15) {
                robot.LFront.setPower(FORWARD_SPEED);
                robot.LBack.setPower(-FORWARD_SPEED);
                robot.RFront.setPower(FORWARD_SPEED);
                robot.RBack.setPower(-FORWARD_SPEED);
            }
        }
        stopMotors();
    }


    public void pivot(String dir, int time) {


        if (dir.equals("L")) {
            robot.LFront.setPower(-FORWARD_SPEED);
            robot.LBack.setPower(-FORWARD_SPEED);
            robot.RFront.setPower(FORWARD_SPEED);
            robot.RBack.setPower(FORWARD_SPEED);
        } else if (dir.equals("R")) {
            robot.LFront.setPower(FORWARD_SPEED);
            robot.LBack.setPower(FORWARD_SPEED);
            robot.RFront.setPower(-FORWARD_SPEED);
            robot.RBack.setPower(-FORWARD_SPEED);
        }
        sleep(time);
        stopMotors();
    }


    public void forward(int time) {
        robot.LFront.setPower(FORWARD_SPEED);
        robot.LBack.setPower(FORWARD_SPEED);
        robot.RFront.setPower(FORWARD_SPEED);
        robot.RBack.setPower(FORWARD_SPEED);
        sleep(time);
        stopMotors();
    }


    public void backward(int ticks) {
        int lbeck = robot.LBack.getCurrentPosition()+ticks;
        robot.LBack.setTargetPosition(lbeck);
        int rback = robot.RBack.getCurrentPosition()+ticks;
        robot.RBack.setTargetPosition(rback);

        while(Math.abs(rback - robot.RBack.getCurrentPosition()) > 15 && Math.abs(lbeck - robot.LBack.getCurrentPosition()) > 15)
        {

            //robot.LFront.setPower(FORWARD_SPEED);
            robot.LBack.setPower(-FORWARD_SPEED);
            //robot.RFront.setPower(-FORWARD_SPEED);
            robot.RBack.setPower(-FORWARD_SPEED);
        }
        //sleep(time);
        stopMotors();
    }


    public void stopMotors() {
        robot.LFront.setPower(0);
        robot.LBack.setPower(0);
        robot.RFront.setPower(0);
        robot.RBack.setPower(0);
    }
    public void align() {
        // Set clipping pos
        robot.Neck.setPosition(0.45);
        // Close the claw
        robot.Claw.setPosition(0.4);
        // rotate to up position
        robot.ClawRotate.setPosition(0.75);
        robot.Mouth.setPosition(.45);
        // retract the slides
        robot.LExtendo.setPosition(-.8);
        robot.RExtendo.setPosition(-.8);
    }


    public void slides(String dir, int height) {

        int slideHeight = height;
        telemetry.addData("method", "method");

        robot.RSlide.setTargetPosition(height);
        telemetry.addData("rightslidepos", robot.RSlide.getCurrentPosition());

        robot.LSlide.setTargetPosition(-height);
        telemetry.addData("leftslidepos", robot.LSlide.getCurrentPosition());


        if (dir == "up" ) {
            while (Math.abs(robot.LSlide.getCurrentPosition() - slideHeight) > 10 && Math.abs(robot.RSlide.getCurrentPosition() - slideHeight) > 10) {
                robot.LSlide.setPower((-0.6));
                robot.RSlide.setPower((0.6));
            }


            robot.LSlide.setPower(0);
            robot.RSlide.setPower(0);
        }
        else if (dir == "down") {
            while (Math.abs(robot.LSlide.getCurrentPosition() - slideHeight) > 10 && Math.abs(robot.RSlide.getCurrentPosition() - slideHeight) > 10) {
                robot.LSlide.setPower((0.6));
                robot.RSlide.setPower((-0.6));
            }


            robot.LSlide.setPower(0);
            robot.RSlide.setPower(0);

        }


    }







    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        boolean slidesOff = true;

        robot.LBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        robot.LSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        robot.RSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.LBack.setTargetPosition(0);
        robot.RBack.setTargetPosition(0);

        robot.LBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.RBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        telemetry.addData("Status", "Ready to run");



        waitForStart();

        telemetry.addData("run", "runnig");
        //strafe("R", -500, 500);

        // code

        backward(600);
        align();

        slides("up", 1650);
        robot.LSlide.setPower(-0.05);
        robot.RSlide.setPower(0.05);

        backward(1060);
        robot.LSlide.setPower(0.7);
        robot.RSlide.setPower(-0.7);
        backward(150);
        sleep(600);

        robot.LSlide.setPower(-0.05);
        robot.RSlide.setPower(0.05);
        backward(-600);
        slides("down", 0);
        //slides("up",1300);

        sleep(300);



        /*
        slides("down", 500);
        backward(-500);
        telemetry.update();
        sleep(10000);
*/


//        backward(1000+500);
//        slides(1500);
//        backward(-1500);
//        slides(0);
//        strafe("L", 500, 500);




    }
}


