package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="BucketBot", group="Linear OpMode")

public class MecanumDrive extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    HardwareMapFTC robot = new HardwareMapFTC();

    // TODO: Replace this section/method of slide positioning with a PID Controller
    int yCounter = 0;
    //Make robot stay up while undergoing the hanging operation
    boolean Hang = false;


//    public void Scoring() {
//        leftArm.setDirection(Servo.Direction.FORWARD);
//        rightArm.setDirection(Servo.Direction.FORWARD);
//        clawRotate.setPosition(.6);
//        leftArm.setPosition(.6);
//        rightArm.setPosition(.6);
//    }
    public void Collection() {
        robot.LArm.setDirection(Servo.Direction.REVERSE);
        robot.RArm.setDirection(Servo.Direction.REVERSE);
        robot.LArm.setPosition(.03);
        robot.RArm.setPosition(.07);
        robot.BucketLid.setPosition(.80);
    }


    @Override
    public void runOpMode() {
        double slidesDefaultPower = 1;

        // Initialize the hardware variables from the HardwareMapFTC class.
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // TODO: Replace this section/method of slide positioning with a PID Controller
        // Variable for the hang slides, holds the current position of the slides.
        int slideHeight = 0;
        // Reset the hang slide encoders
        robot.LSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.LSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.RSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);



        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Makes it easier to create a variable power level
            double PowerCoefficient = .75;
            double PowerCoefficient2 = .75;

            // Makes the motors move at full speed
            if (gamepad1.right_bumper) {
                PowerCoefficient = 1.0;
            }
            // Extra accuracy mode
            if (gamepad1.left_bumper) {
                PowerCoefficient = 0.25;
            }

            // Extra accuracy mode GamePad 2
            if (gamepad1.left_bumper) {
                PowerCoefficient2 = 0.25;
            }

            // Makes the motors move at full speed GamePad1
            if (gamepad2.right_bumper) {
                PowerCoefficient2 = 1.0;
            }



            // input: theta, power, and turn
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double turn = gamepad1.right_trigger - gamepad1.left_trigger;

            double theta = Math.atan2(y, x);
            double power = Math.hypot(x, y);

            double sin = Math.sin(theta - Math.PI / 4);
            double cos = Math.cos(theta - Math.PI / 4);
            double max = Math.max(Math.abs(sin), Math.abs(cos));

            double leftFrontPower = power * cos / max + turn;
            double rightFrontPower = power * sin / max - turn;
            double leftRearPower = power * sin / max + turn;
            double rightRearPower = power * cos / max - turn;

            if ((power + Math.abs(turn)) > 1) {
                leftFrontPower /= power + turn;
                rightFrontPower /= power + turn;
                leftRearPower /= power + turn;
                rightRearPower /= power + turn;
            }
            if (gamepad1.dpad_up) {
                // Extendo
                robot.LExtendo.setPosition(0);
                robot.RExtendo.setPosition(1);
            }
            if (gamepad1.dpad_down) {
                // Retracto
                robot.LExtendo.setPosition(1);
                robot.RExtendo.setPosition(0);
            }

            // TODO: Replace this section/method of slide positioning with a PID Controller
            if (robot.RSlide.getCurrentPosition() > 10 && robot.LSlide.getCurrentPosition() > 10) {
                robot.RSlide.setPower(slidesDefaultPower);
                robot.LSlide.setPower(slidesDefaultPower);
            }

            // For the Slide(s)
            // TODO: make sure autos bring slides down at the end
            if(gamepad2.y){
                yCounter +=1;
                sleep(150);
            }
            if(gamepad2.a){
                Collection();
                slideHeight = 0;
                yCounter =0;
            }


            // change positions when y is pressed
            // TODO: set the slide heights
            switch (yCounter) {
                case 1:
                    slideHeight = 1300;
                    sleep(50);
                   // Scoring();
                    break;
                case 2:
                    slideHeight = 1750;
                    sleep(50);
                   // Scoring();
                    break;
                case 3:
                    slideHeight = 2250;
                    sleep(50);
                   // Scoring();
                    break;
                case 4:
                    yCounter = 0;
                    break;
            }




            // Set the power of the motors
            robot.LFront.setPower(leftFrontPower * PowerCoefficient);
            robot.RFront.setPower(rightFrontPower * PowerCoefficient);
            robot.LBack.setPower(leftRearPower * PowerCoefficient);
            robot.RBack.setPower(rightRearPower * PowerCoefficient);

            // TODO: Replace this section/method of slide positioning with a PID Controller
            // Set the slides to the target position
            robot.LSlide.setTargetPosition(-slideHeight);
            robot.RSlide.setTargetPosition(slideHeight);
            robot.LSlide.setPower(-1);
            robot.RSlide.setPower(1);




            // Telemetry
            //      Show runtime
            telemetry.addData("Status",           "Run Time: " + runtime.toString());
            //      Show motor power
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftRearPower, rightRearPower);
            //      Show stick positions
            telemetry.addData("Left Stick",       "%4.2f, %4.2f", gamepad1.left_stick_x, gamepad1.left_stick_y);
            telemetry.addData("Right Stick",      "%4.2f, %4.2f", gamepad1.right_stick_x, gamepad1.right_stick_y);
            //      Show triggers
            telemetry.addData("Right Trigger",    "%4.2f", gamepad1.right_trigger);
            telemetry.addData("Left Trigger",     "%4.2f", gamepad1.left_trigger);


        }
    }
}