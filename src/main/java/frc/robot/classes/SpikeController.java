package frc.robot.classes;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class SpikeController {
    private final XboxController m_controller;
    private final double m_deadband;
    private final String m_name;

    /**
     * A generic controller class that handles deadband and stick direction on top of the XboxController class
     * @param controller The controller to use
     * @param deadband The deadband to use
     * @param name The name of the controller
     */
    public SpikeController(XboxController controller, double deadband, String name) {
        m_controller = controller;
        m_name = name;
        m_deadband = MathUtil.clamp(deadband, 0.0d, 1.0d);
    }

    /**
     * Checks if the given value is outside of the deadband, otherwise return 0
     * @param value The value to check
     * @param deadband The deadband to use
     */
    public double deadband(double value, double deadband) {
        return Math.abs(value) >= deadband ? value : 0.0d;
    }

    public Rotation2d getLeftDirection() {
        return new Rotation2d(m_controller.getLeftX(), m_controller.getLeftY());
    }

    public Rotation2d getRightDirection() {
        return new Rotation2d(m_controller.getRightX(), m_controller.getRightY());
    }

    public double getLeftMagnitude() {
        double magnitude = Math.sqrt(Math.pow(getLeftX(), 2) + Math.pow(getLeftY(), 2));
        return magnitude;
    }

    public double getRightMagnitude() {
        double magnitude = Math.sqrt(Math.pow(getRightX(), 2) + Math.pow(getRightY(), 2));
        return magnitude;
    }

    public double getLeftX() {
        double axisValue = deadband(m_controller.getLeftX(), m_deadband);
        SmartDashboard.putNumber(m_name + "Left X", axisValue);
        return axisValue;
    }

    public double getLeftY() {
        double axisValue = deadband(m_controller.getLeftY(), m_deadband);
        SmartDashboard.putNumber(m_name + " Left Y", axisValue);
        return axisValue;
    }

    public double getRightX() {
        double axisValue = deadband(m_controller.getRightX(), m_deadband);
        SmartDashboard.putNumber(m_name + " Right X", axisValue);
        return axisValue;
    }

    public double getRightY() {
        double axisValue = deadband(m_controller.getRightY(), m_deadband);
        SmartDashboard.putNumber(m_name + " Right Y", axisValue);
        return axisValue;
    }

    public double getLeftTrigger() {
        return m_controller.getLeftTriggerAxis();
    }

    public double getRightTrigger() {
        return m_controller.getRightTriggerAxis();
    }

    public boolean getLeftBumper() {
        return m_controller.getLeftBumper();
    }

    public boolean getRightBumper() {
        return m_controller.getRightBumper();
    }

    public boolean getAButton() {
        return m_controller.getAButton();
    }

    public boolean getBButton() {
        return m_controller.getBButton();
    }

    public boolean getXButton() {
        return m_controller.getXButton();
    }

    public boolean getYButton() {
        return m_controller.getYButton();
    }

    public enum Button {
        AButton(XboxController.Button.kA.value),
        BButton(XboxController.Button.kB.value),
        XButton(XboxController.Button.kX.value),
        YButton(XboxController.Button.kY.value),
        LeftBumper(XboxController.Button.kLeftBumper.value),
        RightBumper(XboxController.Button.kRightBumper.value);

        public final int index;

        private Button(int index) {
            this.index = index;
        }
    }

    public boolean getButton(Button buttonToGet) {
        switch (buttonToGet) {
            case AButton: return getAButton();
            case BButton: return getBButton();
            case XButton: return getXButton();
            case YButton: return getYButton();
            case LeftBumper: return getLeftBumper();
            case RightBumper: return getRightBumper();
            default: throw new IllegalArgumentException("Invalid Spike Controller Button (should never happen)");
        }
    }

    public JoystickButton getJoystickButton(Button buttonToGet) {
        return new JoystickButton(m_controller, buttonToGet.index);
    }
}
