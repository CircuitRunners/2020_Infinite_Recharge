/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

/**
 * This handles everything related to the wheel of fortune system.
 */
public class WheelOfFortune {
    private static final I2C.Port i2cPort = I2C.Port.kOnboard;
    private static final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
    private static final ColorMatch m_colorMatcher = new ColorMatch();

    private static final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private static final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private static final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private static final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    private static TalonSRX spinner;
    public static void init(){
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget);

        spinner = new TalonSRX(14);
        configTalon(spinner);
    }
    private static String colorString;

    public void readColorWheel(){
        Color detectedColor = m_colorSensor.getColor();
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
        if (match.color == kBlueTarget) {
            colorString = "Blue";
          } else if (match.color == kRedTarget) {
            colorString = "Red";
          } else if (match.color == kGreenTarget) {
            colorString = "Green";
          } else if (match.color == kYellowTarget) {
            colorString = "Yellow";
          } else {
            colorString = "Unknown";
          }
          SmartDashboard.putNumber("Red", detectedColor.red);
          SmartDashboard.putNumber("Green", detectedColor.green);
          SmartDashboard.putNumber("Blue", detectedColor.blue);
          SmartDashboard.putNumber("Confidence", match.confidence);
          SmartDashboard.putString("Detected Color", colorString);
    }

    public static void level2Spin(){
      spinner.set(ControlMode.MotionMagic, 30000);
    }
    private static String fmsMessage = "";
    public static void level3Spin(){
      switch(fmsMessage){
        case "R":
          if(colorString .equals("Red")){
            spinner.set(ControlMode.MotionMagic, 9000);
          } else {
            spinner.set(ControlMode.PercentOutput, 0.5);
          }
      }
    }
    private static void configTalon(TalonSRX thisTalon){
        thisTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        thisTalon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10);
        thisTalon.config_kP(0, 0.06);
        thisTalon.config_kI(0, 0.0001);
        thisTalon.config_kD(0, 0.05);
        thisTalon.config_kF(0, 0);
        thisTalon.setNeutralMode(NeutralMode.Brake);
    }
}
