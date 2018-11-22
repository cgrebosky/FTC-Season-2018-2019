package c4.lib;

/**
 * Created by Miklos on 11/15/16.
 */

public class PIDController {
    private double proportional = 0;
    private double integral = 0;
    private double derivative = 0;

    public double kp = 0;
    public double ki = 0;
    public double kd = 0;

    public double totalCorrection = 0;
    private double maxIntegral = Double.MAX_VALUE;

    private double prevError = 0;

    public PIDController() {}
    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }
    public PIDController(double kp, double ki, double kd, double max) {
        this(kp, ki, kd);
        maxIntegral = max;
    }

    public double calculateError(double error) {
        proportional = error;

        integral += error;
        if (Math.abs(integral) > Math.abs(maxIntegral)) {
            integral = maxIntegral;
        }

        derivative = prevError - error;
        prevError = error;

        totalCorrection = (kp*proportional) + (ki*integral) + (kd*derivative);

        return (totalCorrection);
    }
    public void update(double error) {
        calculateError(error);
    }

    public double getPrevCorrection() {
        return prevError * kp;
    }

    public void reset() {
        proportional = 0;
        integral = 0;
        derivative = 0;

        prevError = 0;
        totalCorrection = 0;
    }
}
