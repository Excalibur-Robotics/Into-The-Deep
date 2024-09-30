package org.firstinspires.ftc.teamcode;
import com.outoftheboxrobotics.photoncore.hardware.motor.PhotonDcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="AnsweredPrayers", group="Linear OpMode")

public class PhotonTest extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    // Count the number of times that "y" is pressed
    int yCounter=0;

    private PhotonDcMotor frontLeft = null;
    private PhotonDcMotor frontRight = null;
    private PhotonDcMotor backLeft = null;
    private PhotonDcMotor backRight = null;
    private PhotonDcMotor slideL = null;
    private PhotonDcMotor slideR = null;
    private PhotonDcMotor Intake = null;
    private Servo leftArm = null;
    private Servo rightArm = null;
    private Servo clawRotate = null;
    private CRServo pixelWheel = null;
    private Servo leftClamp = null;
    private Servo rightClamp = null;

    public void Scoring() {
        leftArm.setDirection(Servo.Direction.FORWARD);
        rightArm.setDirection(Servo.Direction.FORWARD);
        clawRotate.setPosition(.6);
        leftArm.setPosition(.6);
        rightArm.setPosition(.6);
    }
    public void Collection() {
        leftArm.setDirection(Servo.Direction.REVERSE);
        rightArm.setDirection(Servo.Direction.REVERSE);
        leftArm.setPosition(.03);
        rightArm.setPosition(.07);
        clawRotate.setPosition(.80);
    }
    @Override
    public void runOpMode() {
        double slidesDefaultPower = 1;

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        //Drive motors
        frontLeft = hardwareMap.get(DcMotorEx.class, "Rear Left");
        frontRight = hardwareMap.get(DcMotorEx.class, "Rear Right");
        backLeft = hardwareMap.get(DcMotorEx.class, "Front Left");
        backRight = hardwareMap.get(DcMotorEx.class, "Front Right");

        //Bucket Servos
        leftArm = hardwareMap.get(Servo.class,"LeftArm");
        rightArm = hardwareMap.get(Servo.class, "RightArm");
        clawRotate = hardwareMap.get(Servo.class, "clawRotate");
        pixelWheel = hardwareMap.get(CRServo.class, "pixelWheel");


        // Linear slides
        // Slide Motors
        slideL = hardwareMap.get(DcMotorEx.class, "SlideL");
        slideR = hardwareMap.get(DcMotorEx.class, "SlideR");

        // Intake Motors
        Intake = hardwareMap.get(DcMotorEx.class, "Intake");

        // Intake Servos
        leftClamp  = hardwareMap.get(Servo.class, "clampL");
        rightClamp = hardwareMap.get(Servo.class, "clampR");


//        // Plane Launcher
//        // Servos

        CRServo drone = hardwareMap.get(CRServo.class, "Drone Launcher");

        // This is on the expansion hub


        // The left wheels are opposite on the robot
        // This code compensates
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);


        // Makes the robot resist inertia when no power is applied
        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        slideL.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        slideR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        Intake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        // Allows slides to be set in a position
        slideL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideL.setTargetPositionTolerance(50);
        slideR.setTargetPositionTolerance(50);



        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        int slideHeight = 0;
        slideL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Makes it easier to create a variable power level
            double PowerCoefficient = 1.0;
            double PowerCoefficient2 = 1.0;


            // Makes the motors move at half speed
            if (gamepad1.right_bumper) {
                PowerCoefficient = .5;
            }
            // Extra accuracy mode
            if (gamepad1.left_bumper) {
                PowerCoefficient = 0.25;
            }

            /*
            // Extra accuracy mode GamePad 2
            if (gamepad2.left_bumper) {
                PowerCoefficient2 = 0.25;
            }

            // Makes the motors move at half speed GamePad2
            if (gamepad2.right_bumper) {
                PowerCoefficient2 = .5;
            }
            */

            drone.setPower(0);
            if (slideR.getCurrentPosition() > 10 && slideL.getCurrentPosition() > 10) {
                slideR.setPower(slidesDefaultPower);
                slideL.setPower(slidesDefaultPower);
            }

            // input: theta, power, and turn
            // fancy math to make movements precise
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double turn = gamepad1.right_trigger - gamepad1.left_trigger;
            double intakePower = -gamepad2.left_stick_y;

            double theta = Math.atan2(y, x);
            double power = Math.hypot(x, y);

            double sin = Math.sin (theta - Math.PI/4);
            double cos = Math.cos (theta - Math.PI/4);
            double max = Math.max(Math.abs(sin), Math.abs(cos));

            double leftFrontPower = power * cos/max + turn;
            double rightFrontPower = power * sin/max - turn;
            double leftRearPower = power * sin/max + turn;
            double rightRearPower = power * cos/max - turn;

            // pretty sure this prevents over powering the motors
            if ((power + Math.abs (turn)) > 1) {
                leftFrontPower /=  power + turn;
                rightFrontPower /= power + turn;
                leftRearPower /= power + turn;
                rightRearPower /= power + turn;
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
            // For Drone Launcher
            if(gamepad2.ps){
                drone.setPower(-1);
            }

            // change positions when y is pressed
            // TODO: set the slide heights
            switch (yCounter) {
                case 1:
                    slideHeight = 1300;
                    sleep(50);
                    Scoring();
                    break;
                case 2:
                    slideHeight = 1750;
                    sleep(50);
                    Scoring();
                    break;
                case 3:
                    slideHeight = 2250;
                    sleep(50);
                    Scoring();
                    break;
                case 4:
                    yCounter = 0;
                    break;
            }



            //clamps
            //TODO: set positions
            if (gamepad2.left_bumper) {
                leftClamp.setPosition(.55);
                // sleep(75);
                rightClamp.setPosition(.25);

            }
            if (gamepad2.right_bumper) {
                rightClamp.setPosition(1);
                leftClamp.setPosition(1);
            }






            frontLeft.setPower(leftFrontPower*PowerCoefficient);
            frontRight.setPower(rightFrontPower*PowerCoefficient);
            backLeft.setPower(leftRearPower*PowerCoefficient);
            backRight.setPower(rightRearPower*PowerCoefficient);

            slideL.setTargetPosition(-slideHeight);
            slideR.setTargetPosition(slideHeight);

            Intake.setPower(intakePower*PowerCoefficient);
            pixelWheel.setPower(intakePower*PowerCoefficient);


            slideL.setPower(-1);
            slideR.setPower(1);




            // Show the elapsed game time and wheel power.
            telemetry.addData("Status",           "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftRearPower, rightRearPower);
            telemetry.addData("Left Stick",       "%4.2f, %4.2f", gamepad1.left_stick_x, gamepad1.left_stick_y);
            telemetry.addData("Right Stick",      "%4.2f, %4.2f", gamepad1.right_stick_x, gamepad1.right_stick_y);
            telemetry.addData("Right Trigger",    "%4.2f", gamepad1.right_trigger);
            telemetry.addData("Left Trigger",     "%4.2f", gamepad1.left_trigger);

            // Show Slide positions
            float rightSlideData = (float) slideR.getCurrentPosition();
            float leftSlideData = (float) slideL.getCurrentPosition();
            telemetry.addData("Right Slide","%4.2f", rightSlideData);
            telemetry.addData("Left Slide","%4.2f", leftSlideData); //-500
            telemetry.addData("Left slide motor power","%4.2f", slideL.getPower()); //-500
            float castSlideHeight = (float) slideHeight;
            telemetry.addData("Slide Height: ","%4.2f", castSlideHeight); //-500

            telemetry.update();
        }
    }}
