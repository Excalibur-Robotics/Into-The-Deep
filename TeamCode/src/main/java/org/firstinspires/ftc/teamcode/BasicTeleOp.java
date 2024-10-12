package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleOpBasic", group="Linear OpMode")

public class BasicTeleOp extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    // Count the number of times that "y" is pressed
    int yCounter=0;

    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotorEx slideL = null;
    private DcMotorEx slideR = null;
    private Servo rotationalServo = null;
    private CRServo intakeServo = null;


    @Override
    public void runOpMode() {
        double slidesDefaultPower = 1;

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        //Drive motors
        frontLeft = hardwareMap.get(DcMotorEx.class, "Front Left");
        frontRight = hardwareMap.get(DcMotorEx.class, "Front Right");
        backLeft = hardwareMap.get(DcMotorEx.class, "Rear Left");
        backRight = hardwareMap.get(DcMotorEx.class, "Rear Right");



        // Linear slides
        // Slide Motors
        slideL = hardwareMap.get(DcMotorEx.class, "SlideL");
        slideR = hardwareMap.get(DcMotorEx.class, "SlideR");

        // intake servos

        rotationalServo = hardwareMap.get(Servo.class, "intakePivot");
        intakeServo = hardwareMap.get(CRServo.class, "intakeServo");





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




            if (slideR.getCurrentPosition() > 10 && slideL.getCurrentPosition() > 10) {
                slideR.setPower(slidesDefaultPower);
                slideL.setPower(slidesDefaultPower);
            }

            // input: theta, power, and turn
            // fancy math to make movements precise
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double turn = gamepad1.right_trigger - gamepad1.left_trigger;
            double intakePower = -gamepad2.right_stick_y;

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

            //mate intake liftoff
            //TODO: Set servo position
            if (gamepad1.right_bumper){
                rotationalServo.setPosition(0.2);
            }

            if (gamepad1.left_bumper){
                rotationalServo.setPosition(0);
            }




            // For the Slide(s)
            // TODO: make sure autos bring slides down at the end
            if(gamepad2.y){
                yCounter +=1;
                sleep(150);
            }
            if(gamepad2.a){

                slideHeight = 0;
                yCounter =0;
            }



            // change positions when y is pressed
            // TODO: set the slide heights
            switch (yCounter) {
                case 1:
                    slideHeight = 1300;
                    sleep(50);

                    break;
                case 2:
                    slideHeight = 1750;
                    sleep(50);

                    break;
                case 3:
                    slideHeight = 2250;
                    sleep(50);

                    break;
                case 4:
                    yCounter = 0;
                    break;
            }


            frontLeft.setPower(leftFrontPower*PowerCoefficient);
            frontRight.setPower(rightFrontPower*PowerCoefficient);
            backLeft.setPower(leftRearPower*PowerCoefficient);
            backRight.setPower(rightRearPower*PowerCoefficient);

            slideL.setTargetPosition(-slideHeight);
            slideR.setTargetPosition(slideHeight);


            slideL.setPower(-1);
            slideR.setPower(1);

            intakeServo.setPower(intakePower);


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
