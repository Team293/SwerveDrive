// RobotBuilder Version: 4.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

// ROBOTBUILDER TYPE: Subsystem.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.util.SwerveConstants;
import frc.robot.SwerveModule;
import frc.robot.classes.Position2D; 

public class Drivetrain extends SubsystemBase {
    public SwerveDriveOdometry m_swerveOdometry;
    public SwerveModule[] m_swerveModules;
    public final AHRS gyro;
   

    public Drivetrain() {
        gyro = new AHRS(SerialPort.Port.kMXP);
        gyro.reset();

        // Create modules from the constant values in SwerveConstants
        m_swerveModules = new SwerveModule[] {
            new SwerveModule(0, SwerveConstants.Swerve.Mod0.constants),
            new SwerveModule(1, SwerveConstants.Swerve.Mod1.constants),
            new SwerveModule(2, SwerveConstants.Swerve.Mod2.constants),
            new SwerveModule(3, SwerveConstants.Swerve.Mod3.constants)
        };

        // By pausing init for a second before setting module offsets, we avoid a bug with inverting motors
        new Thread(() -> {
          try {
            Thread.sleep(1000);
            resetModulesToAbsolute();
          } catch (Exception e) {}
        }).start();

        // Create a new swerve odometry object, similar to the Kinematics.java file before 
        m_swerveOdometry = new SwerveDriveOdometry(SwerveConstants.Swerve.swerveKinematics, getYaw(), getModulePositions());

        AutoBuilder.configureHolonomic(
            this::getPose, // Robot pose supplier
            this::resetOdometry, // Method to reset odometry (will be called if your auto has a starting pose)
            this::getRobotRelativeSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
            this::drive, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
            new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
                new PIDConstants(5.0, 0.0, 0.0), // Translation PID constants
                new PIDConstants(5.0, 0.0, 0.0), // Rotation PID constants
                4.5, // Max module speed, in m/s
                0.4, // Drive base radius in meters. Distance from robot center to furthest module.
                new ReplanningConfig() // Default path replanning config. See the API for the options here
            ),
            this // Reference to this subsystem to set requirements

        );

        Pose2d targetPose = new Pose2d(10, 5, Rotation2d.fromDegrees(180));

        // Create the constraints to use while pathfinding
        PathConstraints constraints = new PathConstraints(
          3.0, 4.0, 
          Units.degreesToRadians(540), Units.degreesToRadians(720));

        // Since AutoBuilder is configured, we can use it to build pathfinding commands
        // Command pathfindingCommand = AutoBuilder.pathfindToPose(
        //      targetPose,
        //      constraints,
        //      0.0, // Goal end velocity in meters/sec
        //      0.0 // Rotation delay distance in meters. This is how far the robot should travel before attempting to rotate.
        // );
    }

    public void drive(Translation2d translation, Rotation2d rotation, boolean fieldRelative, boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates = 
            SwerveConstants.Swerve.swerveKinematics.toSwerveModuleStates(
                fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(),
                                    translation.getY(),
                                    rotation.getRadians(),
                                    getYaw()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(),
                                    translation.getY(),
                                    rotation.getRadians())
                                );
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, SwerveConstants.Swerve.maxSpeed);

        for (SwerveModule module : m_swerveModules) {
            module.setDesiredState(swerveModuleStates[module.m_moduleNumber], isOpenLoop);
        }
    }

    public void drive(ChassisSpeeds speeds) {
        SwerveModuleState[] swerveModuleStates = SwerveConstants.Swerve.swerveKinematics.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, SwerveConstants.Swerve.maxSpeed);
        
        for (SwerveModule module : m_swerveModules) {
            module.setDesiredState(swerveModuleStates[module.m_moduleNumber], false);
        }
    }
    

    public void setNeutralMode(NeutralMode nm) {
        for (SwerveModule module : m_swerveModules) {
            module.setNeutralMode(nm);
        }
    }

    @Override
    public void periodic() {
        m_swerveOdometry.update(getYaw(), getModulePositions());

        // Add the current robot position to smartdashboard
        Pose2d robotTranslation = m_swerveOdometry.getPoseMeters();
        SmartDashboard.putNumber("Robot X (SwerveOdometry)", robotTranslation.getTranslation().getX());
        SmartDashboard.putNumber("Robot Y (SwerveOdometry)", robotTranslation.getTranslation().getY());
        SmartDashboard.putNumber("Robot Angle (SwerveOdometry)", robotTranslation.getRotation().getDegrees());

        for (SwerveModule module : m_swerveModules) {
            SmartDashboard.putNumber("Mod " + module.m_moduleNumber + " Cancoder", module.getCanCoder().getDegrees());
            SmartDashboard.putNumber("Mod " + module.m_moduleNumber + " Angle", module.getPosition().angle.getDegrees());
            SmartDashboard.putNumber("Mod " + module.m_moduleNumber + " Meters/Sec", module.getState().speedMetersPerSecond);
        }
    }

    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, SwerveConstants.Swerve.maxSpeed);

        for (SwerveModule module : m_swerveModules) {
            module.setDesiredState(desiredStates[module.m_moduleNumber], false);
        }
    }

    public Pose2d getPose() {
        return m_swerveOdometry.getPoseMeters();
    }

    public void resetOdometry(Pose2d pose) {
        m_swerveOdometry.resetPosition(getYaw(), getModulePositions(), pose);
    }

    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] states = new SwerveModuleState[m_swerveModules.length];
        for (SwerveModule module : m_swerveModules) {
            states[module.m_moduleNumber] = module.getState();
        }

        return states;
    }

    public SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] positions = new SwerveModulePosition[m_swerveModules.length];
        for (SwerveModule module : m_swerveModules) {
            positions[module.m_moduleNumber] = module.getPosition();
        }

        return positions;
    }

    public void zeroGyro() {
        gyro.zeroYaw();
    }

    public Rotation2d getYaw() {
        return Rotation2d.fromDegrees(gyro.getAngle());
    }

    public void resetModulesToAbsolute() {
        for (SwerveModule module : m_swerveModules) {
            module.resetToAbsolute();
        }
    }

    public void initAutonomous(Position2D startingPose) {
        resetOdometry(new Pose2d(startingPose.getX(), startingPose.getY(), new Rotation2d(startingPose.getHeadingRadians())));
    }

    

    public Command getAutonomousCommand(){
        return new PathPlannerAuto("Example Auto");
    }

    public void stop() {
        drive(new Translation2d(0, 0), getYaw(), true, false);
    }

    public ChassisSpeeds getRobotRelativeSpeeds(){
        SwerveModuleState[] swerveModuleStates = getModuleStates();
        return SwerveConstants.Swerve.swerveKinematics.toChassisSpeeds(swerveModuleStates);
    }
}
