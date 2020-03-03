/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.util.Units;

/**
 * The drivebase. what do you think it does?
 */
public class Drivebase {
  static WPI_TalonFX left1;
  static WPI_TalonFX left2;
  static WPI_TalonFX right1;
  static WPI_TalonFX right2;
  static SpeedControllerGroup left;
  static SpeedControllerGroup right;
  public static final int LEFT1_ID = 10;
  public static final int LEFT2_ID = 11;
  public static final int RIGHT1_ID = 12;
  public static final int RIGHT2_ID = 13;
  static ADXRS450_Gyro gyro;
  private static DifferentialDrive drive;
  public DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(0.55245);
  public DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(getHeading());
  public static Pose2d position = new Pose2d();

  private static final int METERS_TO_CLICKS = 000;//math this out pls

  public static void init(){
        gyro = new ADXRS450_Gyro();

        left1 = new WPI_TalonFX(LEFT1_ID);
        left2 = new WPI_TalonFX(LEFT2_ID);
        right1 = new WPI_TalonFX(RIGHT1_ID);
        right2 = new WPI_TalonFX(RIGHT2_ID);

        configTalon(left1);
        configTalon(left2);
        configTalon(right1);
        configTalon(right2);

        left = new SpeedControllerGroup(left1, left2);
        right = new SpeedControllerGroup(right1, right2);

        drive = new DifferentialDrive(left, right);
    }

  private static double prevX = 0.0;
  private static double prevT = 0.0;

  private static double safety(double cmdVal, double prevVal, double maxChange) {
		double diff = cmdVal - prevVal;
		if (Math.abs(diff) < maxChange) {
			return cmdVal;
		} else {
			if (diff > 0) {
				return prevVal + maxChange;
			} else {
				return prevVal - maxChange;
			}
		
        }
    }
  private static double smooth(double value, double deadBand, double max) {
      double aValue = Math.abs(value);
      if (aValue > max)
        return (value / aValue);
      else if (aValue < deadBand)
        return 0;
      else
        return aValue * (value / aValue);
      }   
  public static void drive(double x, double t) { // smoothes driving control and prevents brownout from stop to max speed
    x = smooth(x, 0.1, 0.9);
    x = safety(x, prevX, 0.1);
    //left1.set(ControlMode.PercentOutput, x);
    t = smooth(t, 0.1, 0.9);
    t = safety(t, prevT, 0.1);
    //right1.set(ControlMode.PercentOutput, t);
    drive.arcadeDrive(x, t);
    prevX = x;
    prevT = t;
  }
 
  private static final int DRIVING = 100;
  private static final int AUTON = 200;
  private static final int IDLE = 000;
  private static int JOB = 000;

  public static boolean isIdle(){
    return (JOB == IDLE);
  }

  private static void configTalon(WPI_TalonFX falcon){
    falcon.clearStickyFaults();
    falcon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 10);
    falcon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10);
    falcon.config_kP(0, 0.06);
    falcon.config_kI(0, 0.0001);
    falcon.config_kD(0, 0.05);
    falcon.config_kF(0, 0);
    falcon.setNeutralMode(NeutralMode.Brake);
  }

  public Rotation2d getHeading(){
    return Rotation2d.fromDegrees(-gyro.getAngle());
  }

  public static DifferentialDriveWheelSpeeds getSpeeds(){
    return new DifferentialDriveWheelSpeeds(
      left1.getSelectedSensorVelocity() * 10 / 2048 / 10.72 * (2* Math.PI * Units.inchesToMeters(3.0)),
      right1.getSelectedSensorVelocity() * 10 / 2048 / 10.72 * (2* Math.PI * Units.inchesToMeters(3.0))
      );
  }
  public void updateOdometry(){
    position = odometry.update(getHeading(), getSpeeds().leftMetersPerSecond, getSpeeds().rightMetersPerSecond);
  }

}
