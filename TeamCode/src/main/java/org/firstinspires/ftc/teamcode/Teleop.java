package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "DannyTheDinoBot", group = "Linear OpMode")
public class Teleop extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private HardwareMap robot = new HardwareMap();

    private static final double SLIDES_POWER = 1.0;
    private static final double CLAW_OPEN_POSITION = 0.0;
    private static final double CLAW_CLOSED_POSITION = 0.4;
    private static final double MOUTH_OPEN_POSITION = 0.45;
    private static final double MOUTH_CLOSED_POSITION = 1.0;

    private int slideHeight = 0;
    private int yCounter = 0;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        initializeSlides();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            handleDrive();
            handleGamepad2Controls();
            handleSlideControl();

            displayTelemetry();
        }
    }

    private void initializeSlides() {
        robot.LSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.RSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.LSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.RSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.LSlide.setTargetPositionTolerance(50);
        robot.RSlide.setTargetPositionTolerance(50);
    }

    private void handleDrive() {
        double powerCoefficient = gamepad1.right_bumper ? 1.0 : (gamepad1.left_bumper ? 0.25 : 0.75);

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

        if (power + Math.abs(turn) > 1) {
            leftFrontPower /= power + Math.abs(turn);
            rightFrontPower /= power + Math.abs(turn);
            leftRearPower /= power + Math.abs(turn);
            rightRearPower /= power + Math.abs(turn);
        }

        robot.LFront.setPower(leftFrontPower * powerCoefficient);
        robot.RFront.setPower(rightFrontPower * powerCoefficient);
        robot.LBack.setPower(leftRearPower * powerCoefficient);
        robot.RBack.setPower(rightRearPower * powerCoefficient);
    }

    private void handleGamepad2Controls() {
        if (gamepad2.dpad_up) {
            extendArm();
        }
        if (gamepad2.dpad_down) {
            retractArm();
        }
        if (gamepad2.right_trigger > 0.5) {
            robot.Claw.setPosition(CLAW_CLOSED_POSITION);
        }
        if (gamepad2.left_trigger > 0.5) {
            robot.Claw.setPosition(CLAW_OPEN_POSITION);
        }
        if (gamepad2.b) {
            robot.Mouth.setPosition(MOUTH_CLOSED_POSITION);
        }
        if (gamepad2.x) {
            robot.Mouth.setPosition(MOUTH_OPEN_POSITION);
        }
        if (gamepad2.left_bumper) {
            setToCollectionPosition();
        }
        if (gamepad2.right_bumper) {
            setToScoringPosition();
        }
    }

    private void handleSlideControl() {
        if (gamepad2.y) {
            yCounter = Math.min(yCounter + 1, 3);
        }
        if (gamepad2.a) {
            slideHeight = 0; // Set target position to 0
            yCounter = 0;

            // Check if slides are within tolerance of the target position
            if (Math.abs(robot.LSlide.getCurrentPosition() - slideHeight) <= 10 &&
                    Math.abs(robot.RSlide.getCurrentPosition() - slideHeight) <= 10) {
                // Stop motor power when within tolerance
                robot.LSlide.setPower(0);
                robot.RSlide.setPower(0);
            } else {
                // Continue moving toward the target position
                robot.LSlide.setTargetPosition(-slideHeight);
                robot.RSlide.setTargetPosition(slideHeight);
                robot.LSlide.setPower(SLIDES_POWER);
                robot.RSlide.setPower(SLIDES_POWER);
            }

            setToCollectionPosition(); // Optional: Move to the collection position
        }

        setSlideHeight();

        robot.LSlide.setTargetPosition(-slideHeight);
        robot.RSlide.setTargetPosition(slideHeight);
        robot.LSlide.setPower(SLIDES_POWER);
        robot.RSlide.setPower(SLIDES_POWER);
    }

    private void setSlideHeight() {
        switch (yCounter) {
            case 1:
                slideHeight = 1300;
                break;
            case 2:
                slideHeight = 1750;
                break;
            case 3:
                slideHeight = 2750;
                break;
            default:
                yCounter = 0;
        }
    }

    private void extendArm() {
        robot.Claw.setPosition(CLAW_CLOSED_POSITION);
        robot.ClawRotate.setPosition(1);
        robot.LExtendo.setPosition(0.25);
        robot.RExtendo.setPosition(0.25);
        robot.ClawRotate.setPosition(0.4);
    }

    private void retractArm() {
        robot.Claw.setPosition(CLAW_CLOSED_POSITION);
        robot.ClawRotate.setPosition(1);
        robot.Mouth.setPosition(MOUTH_OPEN_POSITION);
        robot.LExtendo.setPosition(-1);
        robot.RExtendo.setPosition(-1);
        sleep(1000);
        robot.Claw.setPosition(CLAW_OPEN_POSITION);
    }

    private void setToScoringPosition() {
        robot.Neck.setPosition(-1);
        sleep(250);
    }

    private void setToCollectionPosition() {
        robot.Neck.setPosition(1);
        robot.Mouth.setPosition(MOUTH_OPEN_POSITION);
    }

    private void displayTelemetry() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Slide Height", slideHeight);
        telemetry.addData("Y Counter", yCounter);
        telemetry.update();
    }
}