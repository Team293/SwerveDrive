package frc.lib.util;

public class PIDHelper {
    private final double kP;
    private final double kI;
    private final double kD;

    private double previousError;
    private double integral;

    public PIDHelper(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        previousError = 0;
        integral = 0;
    }

    public double step(double error, double dt) {
        integral += error * dt;
        double derivative = (error - previousError) / dt;
        double output = kP * error + kI * integral + kD * derivative;
        previousError = error;
        return output;
    }
}
