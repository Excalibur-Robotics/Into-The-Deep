package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.concurrent.TimeUnit;

public class PID {
    public double kP = 0;
    public double kI = 0;
    public double kD = 0;
    private double errorOverTimeMax = 10;
    private double setPoint = 0;
    private double error = 0;
    private double previousError = 0;
    private double errorOverTime = 0;
    private ElapsedTime clock = null;

    public PID(double P, double I, double D, double sp) { //first 3 arguments are the p, i, and d constants for a PID.  They need to be experimentally determined.
        /*
        TUNING INSTRUCTIONS:
        The PID values work as such.
        The P value will give a force/voltage proportional to the distance from the set point the current position.
        The I value will give a force/voltage for accumulated errors over time, usually small errors.  If error is small, this I value should prevent that.
        The D value will give a force/voltage to prevent overshooting.  If the set point is far away from the current position, it may overshoot, this D value prevents this.

        In terms of calculus, the P value is the proportional value, the I value is the integral, and the D value is the derivative.

        The order in which the PID should be tuned is the P value, the D value, then lastly, the I value.
        For all the constants, you need to change orders of magnitude initially then change the number itself.
        Ex. 1 -> 0.1 -> 0.01 -> 0.05 -> 0.035

        For tuning the P value, a good value will be able to hold its position with minimal oscillation.  It does not necessarily need to be at the set point, but it needs to be as close as possible without oscillation.
        For tuning the D value, a good value will be able to dampen the movement of the controller to prevent overshooting.  It doesn't need to be as exact as the P value, but it does need to prevent the controller from overshooting.
        For tuning the I value, a good value will be able to allow the controller to move according to small errors.  It needs to be low enough to prevent overshooting, but high enough to let the controller adjust in a timely manner.
         */
        kP = P;
        kI = I;
        kD = D;
        setPoint = sp;
        error = setPoint - error;
        previousError = error;
        clock = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    public double PIDLoop(double currentPos) {
        double processVar = 0;
        double time = (double)clock.time(TimeUnit.MILLISECONDS) / 1000.0;
        calcErrors(currentPos, time);
        processVar += calcP() + calcI() + calcD(time);
        previousError = error;
        return processVar; //process variable is the voltage to be given to the motor or component.
    }

    public void setSetPoint(double sp) {  //call this function in order to set the set point.
        setPoint = sp;
    }

    public void updateConst(double P, double I, double D) { //update the PID constants if need be.  Usually won't need to be called.
        kP = P;
        kI = I;
        kD = D;
    }

    //these private functions are only used internally to calculate the different values.

    private double calcP() {
        return (error * kP);
    }

    private double calcI() {
        return (double)errorOverTime * (double)kI;
    }

    private double calcD(double elapsedTime) {
        return ((double)(error - previousError)/(double)elapsedTime) * (double)kD;
    }

    private void calcErrors(double currentPos, double elapsedTime) {
        error = setPoint - currentPos;
        errorOverTime += error * elapsedTime;
        if (errorOverTime > errorOverTimeMax) {
            errorOverTime = errorOverTimeMax;
        } else if (errorOverTime < -errorOverTimeMax) {
            errorOverTime = -errorOverTimeMax;
        }
    }
}