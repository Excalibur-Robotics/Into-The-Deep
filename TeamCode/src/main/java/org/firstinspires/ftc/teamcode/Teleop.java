package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "DannyTheDinoBot", group = "Linear OpMode")
public class Teleop extends LinearOpMode {
    // PID Constants
    double Kp = 0.5; // Proportional gain (tune as necessary)
    double Ki = 0.05; // Integral gain (tune as necessary)
    double Kd = 0.1; // Derivative gain (tune as necessary)
    double maxIntegralSum = 1000; // Maximum value for the integral sum to prevent windup
    double alpha = 0.8; // Smoothing factor for the low-pass filter (0 < alpha < 1)
    double lastError = 0;
    double previousFilterEstimate = 0;



    private ElapsedTime runtime = new ElapsedTime();
    HardwareMap robot = new HardwareMap();

    // Slide Constants
    private static final int SLIDE_LEVEL_1 = 1300;
    private static final int SLIDE_LEVEL_2 = 1750;
    private static final int SLIDE_LEVEL_3 = 2750;
    private static final double SLIDE_POWER = 1.0;

    private int slideHeight = 0;
    private int yCounter = 0;

    @Override
    public void runOpMode() {
        // Initialize the hardware
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Reset slide encoders
        robot.LSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.LSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.RSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            handleDriveControls();
            handleSlideControls();
            handleGamepadActions();
            updateTelemetry();
        }
    }

    private void handleDriveControls() {
        double powerCoefficient = gamepad1.left_bumper ? 0.25 : (gamepad1.right_bumper ? 1.0 : 0.75);

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

        robot.LFront.setPower(leftFrontPower * powerCoefficient);
        robot.RFront.setPower(rightFrontPower * powerCoefficient);
        robot.LBack.setPower(leftRearPower * powerCoefficient);
        robot.RBack.setPower(rightRearPower * powerCoefficient);
    }

    private void handleSlideControls() {
        if (gamepad2.y) {
            yCounter++;
            sleep(150); // Debouncing
        }
        if (gamepad2.a) {
            slideHeight = 0;
            yCounter = 0;
            Collection();
        }

        switch (yCounter) {
            case 1:
                slideHeight = SLIDE_LEVEL_1;
                break;
            case 2:
                slideHeight = SLIDE_LEVEL_2;
                Scoring();
                break;
            case 3:
                slideHeight = SLIDE_LEVEL_3;
                Scoring();
                break;
            default:
                yCounter = 0;
        }

        // Calculate PID output for slides
        double leftSlidePower = PIDControl(-slideHeight, robot.LSlide.getCurrentPosition());
        double rightSlidePower = PIDControl(slideHeight, robot.RSlide.getCurrentPosition());

        // Apply PID output to slides
        robot.LSlide.setPower(leftSlidePower);
        robot.RSlide.setPower(rightSlidePower);
    }

    private void handleGamepadActions() {
        if (gamepad2.right_trigger > 0.5) robot.Claw.setPosition(0.4); // Close claw
        if (gamepad2.left_trigger > 0.5) robot.Claw.setPosition(0.0); // Open claw
        if (gamepad2.b) robot.Mouth.setPosition(1); // Close bucket
        if (gamepad2.x) robot.Mouth.setPosition(0.75); // Open bucket
        if (gamepad2.left_bumper) Collection();
        if (gamepad2.right_bumper) Scoring();

        if (gamepad2.dpad_up) Extendo();
        if (gamepad2.dpad_down) Retracto();
    }

    private void updateTelemetry() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Slide Height", slideHeight);
        telemetry.addData("Y Counter", yCounter);
        telemetry.addData("Left Slide Position", robot.LSlide.getCurrentPosition());
        telemetry.addData("Right Slide Position", robot.RSlide.getCurrentPosition());
        telemetry.update();
    }

    // Existing Methods: Extendo, Retracto, Scoring, Collection
    private void Extendo() {
        robot.Claw.setPosition(0.4);
        robot.ClawRotate.setPosition(1);
        robot.LExtendo.setPosition(0.25);
        robot.RExtendo.setPosition(0.25);
        robot.ClawRotate.setPosition(0.4);
    }

    private void Retracto() {
        robot.Claw.setPosition(0.4);
        robot.ClawRotate.setPosition(1);
        robot.Mouth.setPosition(0.75);
        robot.LExtendo.setPosition(-1);
        robot.RExtendo.setPosition(-1);
        sleep(1000);
        robot.Claw.setPosition(0.2);
    }

    private void Scoring() {
        robot.Neck.setPosition(-1);
    }

    private void Collection() {
        robot.Neck.setPosition(1);
        robot.Mouth.setPosition(1);
    }

    private double PIDControl(double reference, double state) {
        // Initialize or update error variables
        double error = reference - state; // Calculate error
        double errorChange = error - lastError; // Change in error
        double integralSum = 0;

        integralSum += error * runtime.seconds(); // Accumulate integral

        // Apply anti-windup limits to integral sum
        if (integralSum > maxIntegralSum) integralSum = maxIntegralSum;
        if (integralSum < -maxIntegralSum) integralSum = -maxIntegralSum;

        // Low-pass filter for derivative term
        double filteredErrorChange = (alpha * previousFilterEstimate) + ((1 - alpha) * errorChange);
        previousFilterEstimate = filteredErrorChange; // Update the filter estimate

        // Derivative calculation
        double derivative = filteredErrorChange / runtime.seconds();

        // PID Output
        double output = (Kp * error) + (Ki * integralSum) + (Kd * derivative);

        // Save state for the next iteration
        lastError = error;

        // Reset the timer
        runtime.reset();

        return output; // Return the PID output
    }
}