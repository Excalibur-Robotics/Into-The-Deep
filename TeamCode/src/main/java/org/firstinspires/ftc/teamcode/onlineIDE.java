package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="onlineIDE", group="Linear OpMode")

public class onlineIDE extends LinearOpMode {

    public DcMotor   LFront     = null;
    public DcMotor   RFront     = null;
    public DcMotor   LBack      = null;
    public DcMotor   RBack      = null;
    public DcMotorEx LSlide     = null;
    public DcMotorEx RSlide     = null;
    public Servo     LExtendo   = null;
    public Servo     RExtendo   = null;
    public Servo     BucketLid  = null;
    public Servo     BucketRotate = null;
    public Servo     Claw       = null;
    public Servo     ClawRotate = null;

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();


    // TODO: Replace this section/method of slide positioning with a PID Controller
    int yCounter = 0;
    //Make robot stay up while undergoing the hanging operation
    boolean Hang = false;


    public void Extendo() {
        // Close the claw
        Claw.setPosition(0.5);
        // rotate to up position
        ClawRotate.setPosition(1);
        // extend the slides
        LExtendo.setPosition(1);
        RExtendo.setPosition(0);
        // rotate to down position
        ClawRotate.setPosition(0.35);
        // open the claw
        Claw.setPosition(0);
    }
    public void Retracto() {
        // Close the claw
        Claw.setPosition(0.5);
        // rotate to up position
        ClawRotate.setPosition(1);
        // retract the slides
        LExtendo.setPosition(0);
        RExtendo.setPosition(1);
    }
    public void Collection() {
        // Collection Postition
        BucketRotate.setPosition(0);
        // Open Bucket
        BucketLid.setPosition(-1);
    }
    public void Scoring() {
        // Scoring Position
        BucketRotate.setPosition(1);
        // Close Bucket
        BucketLid.setPosition(1);
    }


    @Override
    public void runOpMode() {
        double slidesDefaultPower = 1;

        // Initialize the hardware variables from the HardwareMapFTC class.

        LFront     = hardwareMap.get(DcMotor.class,     "FrontLeft");
        RFront     = hardwareMap.get(DcMotor.class,    "FrontRight");
        LBack      = hardwareMap.get(DcMotor.class,     "BackLeft");
        RBack      = hardwareMap.get(DcMotor.class,    "BackRight");
        LSlide     = hardwareMap.get(DcMotorEx.class,  "LeftSlide");
        RSlide     = hardwareMap.get(DcMotorEx.class, "RightSlide");
        LExtendo   = hardwareMap.get(Servo.class,    "LeftExtendo");
        RExtendo   = hardwareMap.get(Servo.class,   "RightExtendo");
        BucketLid  = hardwareMap.get(Servo.class,      "BucketLid");
        BucketRotate = hardwareMap.get(Servo.class, "BucketRotate");
        Claw       = hardwareMap.get(Servo.class,           "Claw");
        ClawRotate = hardwareMap.get(Servo.class,      "ClawRotate");



        LFront.setDirection(DcMotor.Direction.REVERSE);
        LBack.setDirection(DcMotor.Direction.REVERSE);

        LFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);





        // Allows slides to be set in a position
        //TODO: Replace this section/method of slide positioning with a PID Controller
        LSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LSlide.setTargetPositionTolerance(50);
        RSlide.setTargetPositionTolerance(50);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // TODO: Replace this section/method of slide positioning with a PID Controller
        // Variable for the hang slides, holds the current position of the slides.
        int slideHeight = 0;
        // Reset the hang slide encoders
        LSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);




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
            double x = -gamepad1.left_stick_x;
            double y = gamepad1.left_stick_y;
            double turn = -(gamepad1.right_trigger - gamepad1.left_trigger);

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
            if (gamepad2.dpad_up) {
                // Extendo
                Extendo();
            }
            if (gamepad2.dpad_down) {
                // Retracto
                Retracto();
            }
//            if(gamepad2.dpad_left){
//                ClawRotate.setPosition(1);
//            }
//            if(gamepad2.dpad_right){
//                ClawRotate.setPosition(0.35);
//            }

            // TODO: Replace this section/method of slide positioning with a PID Controller
            if (RSlide.getCurrentPosition() > 10 && LSlide.getCurrentPosition() > 10) {
                RSlide.setPower(slidesDefaultPower);
                LSlide.setPower(slidesDefaultPower);
            }

            // For the Slide(s)
            // TODO: make sure autos bring slides down at the end
            if(gamepad2.y){
                yCounter +=1;
                sleep(150);
            }
            if(gamepad2.a){
                slideHeight = 0;
                Collection();
                yCounter =0;
            }
            if (gamepad2.right_trigger > 0.5) {
                // Close claw
                Claw.setPosition(0.5);
            }
            if (gamepad2.left_trigger > 0.5) {
                // Open claw
                Claw.setPosition(0);
            }
            if (gamepad2.b) {
                // Close bucket
                BucketLid.setPosition(1);
            }
            if (gamepad2.x) {
                // Open bucket
                BucketLid.setPosition(-1);
            }
            if(gamepad2.left_bumper){
                Collection();
            }
            if(gamepad2.right_bumper){
                Scoring();
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
                    Scoring();

                    break;
                case 3:
                    slideHeight = 2750;
                    sleep(50);
                    Scoring();
                    break;
                case 4:
                    yCounter = 0;

                    break;
            }




            // Set the power of the motors
            LFront.setPower(leftFrontPower * PowerCoefficient);
            RFront.setPower(rightFrontPower * PowerCoefficient);
            LBack.setPower(leftRearPower * PowerCoefficient);
            RBack.setPower(rightRearPower * PowerCoefficient);

            // TODO: Replace this section/method of slide positioning with a PID Controller
            // Set the slides to the target position
            LSlide.setTargetPosition(-slideHeight);
            RSlide.setTargetPosition(slideHeight);
            LSlide.setPower(-1);
            RSlide.setPower(1);




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