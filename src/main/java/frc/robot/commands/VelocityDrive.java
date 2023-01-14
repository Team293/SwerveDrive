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

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.math.MathUtil;

import static frc.robot.Constants.DrivetrainConstants.*;

import frc.robot.classes.SPIKE293Utils;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import frc.robot.subsystems.Drivetrain;
// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 *
 */
public class VelocityDrive extends CommandBase 
{
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    private final Drivetrain m_drivetrain;
    private final Joystick m_operatorRightJoy;
    private final Joystick m_operatorLeftJoy;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    private double m_joyDeadband;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

    public VelocityDrive(Drivetrain drivetrain, Joystick operatorRightJoy, Joystick operatorLeftJoy) 
    {
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        m_operatorRightJoy = operatorRightJoy;
        m_operatorLeftJoy = operatorLeftJoy;
        m_drivetrain = drivetrain;
        addRequirements(m_drivetrain);

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        m_joyDeadband = DEFAULT_JOYSTICK_DEADBAND;
        
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() 
    {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() 
    {
        m_joyDeadband = MathUtil.clamp(m_joyDeadband, 0.0d, 1.0d);
        double leftY = 0.0d;
        double rightY = 0.0d;
        double leftVel;
        double rightVel;
        
        // Setting left and right varibales to Joystick positions
        leftY = m_operatorLeftJoy.getY();
        leftY = SPIKE293Utils.applyDeadband(leftY, m_joyDeadband);
        rightY = m_operatorRightJoy.getY();
        rightY = SPIKE293Utils.applyDeadband(rightY, m_joyDeadband);
        
        // Checks to see if triggers is clicked to apply slow down 
        if(m_operatorLeftJoy.getTrigger() || m_operatorRightJoy.getTrigger())
        {
            leftY *= VELOCITY_SLOWDOWN_MODIFIER;
            rightY *= VELOCITY_SLOWDOWN_MODIFIER;
        }

        //Check if throttle should be reversed
        if (m_operatorLeftJoy.getThrottle() < 0)
        {
            leftY *= -1.0d;
            rightY *= -1.0d;
        }

        leftVel = SPIKE293Utils.percentageToControllerVelocity(leftY);
        rightVel = SPIKE293Utils.percentageToControllerVelocity(rightY);
         
        //Send throttle data to velocity drive
        m_drivetrain.velocityDrive(leftVel, rightVel);
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) 
    {
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() 
    {
        return false;
    }

    @Override
    public boolean runsWhenDisabled() 
    {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DISABLED
        return false;

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DISABLED
    }
}
