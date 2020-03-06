/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {



  private static Joystick driver = new Joystick(0);
  //private static XboxController driver = new XboxController(0);
  private static Compressor compressor = new Compressor();
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    Drivebase.init();
    //WheelOfFortune.init();
    //Camera.init();
    Turret.init();
    Intake.init();
    Indexer.init();
    compressor.setClosedLoopControl(true);
  }


  @Override
  public void robotPeriodic() {
    
  }

  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopPeriodic() {
    Indexer.run();
    Drivebase.drive(driver.getRawAxis(Logitech.AXIS_LEFTY), -driver.getRawAxis(Logitech.AXIS_RIGHTX));
    if(driver.getPOV() == 0){
      Turret.speed += 0.01;
    } else if(driver.getPOV() == 180){
      Turret.speed -= 0.01;
    }
    Turret.manualTurret(); 
    SmartDashboard.putNumber("Base angle", Turret.angle);
    SmartDashboard.putNumber("Speed", Turret.flywheel.getSelectedSensorVelocity());
    if(driver.getRawButton(Logitech.BTN_LEFT_BUMPER)){
      Turret.angle -= 1;
    } else if(driver.getRawButton(Logitech.BTN_RIGHT_BUMPER)){
      Turret.angle += 1;
    }
    Turret.manualBase();
    if(driver.getRawButton(Logitech.BTN_A)){
      Intake.in();
      Indexer.up();
    } else if(driver.getRawButton(Logitech.BTN_B)){
      Intake.out();
      Indexer.down();
    } else {
      Intake.stop();
      Indexer.stop();
    }
  }

  @Override
  public void testInit(){
  } 

  @Override
  public void testPeriodic() {

  }

  private static void getControllers(){
/*
    if(driver.getAButton()){
      //Camera.startTracking();
      //Turret.trackTurret();
    } else if(driver.getBButton()){
      //Camera.stopTracking();
      //Turret.shadowTurret();
    }
*/
    if(driver.getRawButton(Logitech.BTN_X)){
      Intake.in();
    } else if(driver.getRawButton(Logitech.BTN_Y)){
      Intake.out();
    } else {
      Intake.stop();
    }
  }
}
