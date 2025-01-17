package org.firstinspires.ftc.teamcode;


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
        } else if (dir.equals("R")) {
            robot.LFront.setPower(TURN_SPEED);
            robot.LBack.setPower(TURN_SPEED);
            robot.RFront.setPower(-TURN_SPEED);
            robot.RBack.setPower(-TURN_SPEED);
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
        int rback = robot.RBack.getCurrentPosition()-ticks;
        robot.RBack.setTargetPosition(rback);

        while(Math.abs(rback - robot.RBack.getCurrentPosition()) > 15 && Math.abs(lbeck - robot.LBack.getCurrentPosition()) > 15)
        {

            //robot.LFront.setPower(FORWARD_SPEED);
            robot.LBack.setPower(FORWARD_SPEED);
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


    public void slides(int height) {
        this.height = height;
        robot.LSlide.setTargetPosition(height);
        robot.RSlide.setTargetPosition(height);
        robot.LSlide.setPower(1);
        robot.RSlide.setPower(1);
        robot.LSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.RSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }






    @Override
    public void runOpMode() {
        robot.init(hardwareMap);


        robot.LBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.LBack.setTargetPosition(0);
        robot.RBack.setTargetPosition(0);
        robot.LBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.RBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        telemetry.addData("Status", "Ready to run");
        telemetry.update();


        waitForStart();
       /*
           Robot is facing the driver
           Strafes to the right
           Raises the slides
           Goes back to the high rise (slowly)
           Lowers slides
           Drives forward
           Stafes to the left to park
        */

        telemetry.addData("run", "runnig");
        //strafe("R", -500, 500);


        // actual code type script
        strafe("R",500,500);
        slides(2000);
        backward(1000+500);
        slides(1500);
        backward(-1500);
        slides(0);
        strafe("L", 500, 500);




    }
}


