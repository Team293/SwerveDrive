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
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib.util.SPIKE293Utils;
import frc.robot.classes.SpikeController;
import frc.robot.subsystems.Drivetrain;

/**
 *
 */
public class OrientedDrive extends CommandBase {
    private final Drivetrain m_drivetrain;
    private final SpikeController m_controller;
    private final boolean m_fieldOriented;

    private final double MAX_TRANSLATION_VELOCITY = 3.0d;
    private final double MAX_ROTATION_VELOCITY_RAD = 0.15d;

    public OrientedDrive(Drivetrain drivetrain, SpikeController controller, boolean fieldOriented) {
        m_drivetrain = drivetrain;
        m_fieldOriented = fieldOriented;
        m_controller = controller;

        addRequirements(m_drivetrain);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        double translationMagnitude = m_controller.getLeftMagnitude() * MAX_TRANSLATION_VELOCITY;
        Rotation2d translationAngle = m_controller.getLeftDirection();

        Translation2d robotTranslation = new Translation2d(translationMagnitude, translationAngle);

        double rotation = m_controller.getRightX() * MAX_ROTATION_VELOCITY_RAD;

        m_drivetrain.drive(robotTranslation, Rotation2d.fromDegrees(rotation), m_fieldOriented, false);

    }
}
