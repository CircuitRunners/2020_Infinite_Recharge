/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Take in bals
 */
public class Intake {
    private static TalonSRX intake;
    private static TalonSRX funnelLeft;
    private static TalonSRX funnelRight;
    private static Solenoid intakeSolenoid = new Solenoid(1);
    public static void init(){
        intake = new TalonSRX(14);
        funnelLeft = new TalonSRX(15);
        funnelRight = new TalonSRX(16);
        
        configTalon(intake, false);
        configTalon(funnelLeft, false);
        configTalon(funnelRight, false);
        //intakeSolenoid.set(Value.kReverse);
    }

    public static void in(){
        intake.set(ControlMode.PercentOutput,-0.75);
        funnelLeft.set(ControlMode.PercentOutput, -.75);
        funnelRight.set(ControlMode.PercentOutput, .75);
        intakeSolenoid.set(true);
    }
    public static void out(){
        intake.set(ControlMode.PercentOutput, 0.75);
        funnelLeft.set(ControlMode.PercentOutput, 0.75);
        funnelRight.set(ControlMode.PercentOutput, -0.75);
        intakeSolenoid.set(false);
    }
    public static void stop(){
        intake.set(ControlMode.PercentOutput, 0);
        funnelLeft.set(ControlMode.PercentOutput, 0);
        funnelRight.set(ControlMode.PercentOutput, 0);
    }

    private static void configTalon(TalonSRX thisTalon, boolean inverted){
        //thisTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        thisTalon.setSensorPhase(!inverted);
        thisTalon.setInverted(inverted);

        thisTalon.selectProfileSlot(0, 0);
        thisTalon.config_kF(0, 0.287, 10);
        thisTalon.config_kP(0, 0.4, 10);
        thisTalon.config_kI(0, 0, 10);
        thisTalon.config_kD(0, 0, 10);
        thisTalon.setSelectedSensorPosition(0, 0, 10);
    }
}
