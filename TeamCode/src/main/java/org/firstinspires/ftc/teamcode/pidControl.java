package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;



import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
/*
public class pidControl extends LinearOpMode{
     HardwareMapFTC robot = new HardwareMapFTC();
    // delete ts DcMotorEx motor;


    double integralSum = 0;
    double Kp = 0;
    double Ki = 0;
    double Kd = 0;


    ElapsedTime timer = new ElapsedTime();
    private double lastError = 0;

    @Override
    public void runOpMode() throws InterruptedException {



        robot.LSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//run w/o motor will decrease performance


        waitForStart();
        while (opModeIsActive()) {
            // changez le reference sous ça parce que ce n'est pas meilleur
            double power = PIDControl(100, robot.LSlide.getCurrentPosition());
            robot.LSlide.setPower(power);
        }

    }



    public double PIDControl(double reference, double state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();


        timer.reset();

        double output = (error *Kp) + (derivative *  Kd)  + (integralSum*Ki);
        return output;



    }

}
*/
