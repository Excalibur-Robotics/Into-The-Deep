package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="ServoReset", group="Linear OpMode")

public class ResetServo extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();


    @Override
    public void runOpMode() {
        Servo servo = hardwareMap.get(Servo.class,"Servo");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.a) {
                // Reset the servo
                servo.setPosition(0);

            }
            if (gamepad1.b) {
                // Move the servo to the other position
                servo.setPosition(1);

            }
            

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status",           "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }}
