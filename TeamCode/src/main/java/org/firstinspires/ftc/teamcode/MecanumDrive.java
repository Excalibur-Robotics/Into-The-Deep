package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="PushBot", group="Linear OpMode")

public class MecanumDrive extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    //Make robot stay up while undergoing the hanging operation
    boolean Hang = false;
    @Override
    public void runOpMode() {







        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();


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

            double SlidePower = 0;

            // input: theta, power, and turn
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double turn = gamepad1.right_trigger - gamepad1.left_trigger;
            double intakePower = .5 * gamepad1.right_stick_y;

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

        }}}