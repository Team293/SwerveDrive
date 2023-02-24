// RobotBuilder Version: 3.1
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.classes.SPIKE293Utils;
import frc.robot.subsystems.Drivetrain;

/**
 *
 */
public class RCFDrive extends CommandBase {
    private final Drivetrain m_drivetrain;
    private final XboxController m_xboxcontroller;

    private double m_rcfDeadband;
    private double m_velocityLimitPercentage;
    private double m_turningLimitPercentage;

    private final double DEFAULT_MAX_VELOCITY_PERCENTAGE = 0.85;
    private final double DEFAULT_MAX_TURNING_SPEED = 0.55d;
    private final double DEFAULT_RCF_JOY_DEADBAND = 0.04;

    public RCFDrive(Drivetrain subsystem, XboxController xboxcontroller) {
        m_drivetrain = subsystem;
        addRequirements(m_drivetrain);
        m_xboxcontroller = xboxcontroller;

        m_velocityLimitPercentage = DEFAULT_MAX_VELOCITY_PERCENTAGE;
        m_turningLimitPercentage = DEFAULT_MAX_TURNING_SPEED;
        m_rcfDeadband = DEFAULT_RCF_JOY_DEADBAND;
        SmartDashboard.putNumber("RCF Joy Deadband", m_rcfDeadband);
        SmartDashboard.putNumber("Max Velocity Percentage", m_velocityLimitPercentage);
        SmartDashboard.putNumber("Max Turning Percentage", m_turningLimitPercentage);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        // Grab values from controller.
        double speed = m_xboxcontroller.getLeftY();
        double turning = m_xboxcontroller.getRightX();

        SmartDashboard.putNumber("RCF Joystick speed", speed);
        SmartDashboard.putNumber("RCF Joystick turning", turning);

        // Grab variables from SmartDashboard and clamping
        m_rcfDeadband = SmartDashboard.getNumber("RCF Joy Deadband", DEFAULT_RCF_JOY_DEADBAND);
        m_rcfDeadband = MathUtil.clamp(m_rcfDeadband, 0.0d, 1.0d);
        m_velocityLimitPercentage = SmartDashboard.getNumber("Max Velocity Percentage",
                DEFAULT_MAX_VELOCITY_PERCENTAGE);
        m_velocityLimitPercentage = MathUtil.clamp(m_velocityLimitPercentage, 0.0d, 1.0d);
        m_turningLimitPercentage = SmartDashboard.getNumber("Max Turning Percentage",
                DEFAULT_MAX_TURNING_SPEED);
        m_turningLimitPercentage = MathUtil.clamp(m_turningLimitPercentage, 0.0d, 1.0d);
        SmartDashboard.putNumber("Max Velocity Percentage", m_velocityLimitPercentage);

        // Applying deadband to turning and speed
        turning = SPIKE293Utils.applyDeadband(turning, m_rcfDeadband);
        speed = SPIKE293Utils.applyDeadband(speed, m_rcfDeadband);

        // Applying percentages
        turning *= m_turningLimitPercentage;
        speed *= m_velocityLimitPercentage;

        SmartDashboard.putNumber("RCF ACTUAL speed", speed);
        SmartDashboard.putNumber("RCF ACTUAL turning", turning);

        // Move robot
        m_drivetrain.arcadeDrive(speed, turning);
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        m_drivetrain.stop();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;
    }
}
