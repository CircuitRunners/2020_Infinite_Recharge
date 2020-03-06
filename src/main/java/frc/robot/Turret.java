/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Turret {
    public static TalonSRX base;
    public static TalonSRX flywheel;
    private static TalonSRX flywheel_slave;
    private static DigitalInput limit;

    private static int basePosition = 0;
    private static int baseRequestedPosition = 0;
    private static double gyroAngle = 0;
    private static double lastGyroAngle = 0;

    private static double baseAngle = 0;

    private static final int CLICK_TO_DEGREE = 130;
    public static final int IDLE = 100;
    public static final int TRACKING = 200;
    public static final int SHADOW = 300;
    public static final int INIT = 400;
    public static int mode = 0;

    public static void init(){
        base = new TalonSRX(17);
        flywheel = new TalonSRX(18);
        flywheel_slave = new TalonSRX(19);

        configTalon(base, false);
        configTalon(flywheel, false);

        flywheel.setInverted(true);
        flywheel_slave.setInverted(true);
        base.setInverted(true);
        base.setSensorPhase(false);
        configTalon(flywheel_slave, false);

        flywheel_slave.follow(flywheel);

        limit = new DigitalInput(0);
        mode = INIT;
        //mode = IDLE;
        speed = 0;
        base.setSelectedSensorPosition(0);
    }
    public static double speed = 0;
    public static void manualTurret(){
        if(speed > 1){
            speed = 1.0;
        } else if (speed < 0){
            speed = 0;
        }
        flywheel.set(ControlMode.PercentOutput, speed);
    }
    public static double angle = 0;
    public static void manualBase(){
        if(angle >= 200){
            angle = 160;
        } else if(angle <= -200){
            angle = -160;
        }
        base.set(ControlMode.Position, angle * CLICK_TO_DEGREE);
    }

    public static void operate(){
        //Everything related to turret operation during the match will be consolidated to here.

    }
    public static void idleTurret(){
        mode = IDLE;
    }
    public static void trackTurret(){
        mode = TRACKING;
    }
    public static void shadowTurret(){
        mode = SHADOW;
    }
    public static void initTurret(){
        mode = INIT;
    }
    private static boolean zeroTurret(){
        if(!limit.get()){
            base.set(ControlMode.PercentOutput, 0.1);
            return false;
        } else {
            base.set(ControlMode.PercentOutput, 0);
            return true;
        }
    }

    public static void commandBase(){
        gyroAngle = Drivebase.gyro.getAngle();
        if(limit.get()){
            basePosition = 0;
        }
        basePosition = base.getSelectedSensorPosition();
        baseAngle = basePosition / CLICK_TO_DEGREE;
        
        if(baseAngle >= 200){
            baseRequestedPosition = -160 * CLICK_TO_DEGREE;
        } else if(baseAngle <= -200){
            baseRequestedPosition = 160 * CLICK_TO_DEGREE;
        }

        if(mode == SHADOW){
            if(gyroAngle > lastGyroAngle){
                baseRequestedPosition -= (int) (gyroAngle - lastGyroAngle) * CLICK_TO_DEGREE;
            } else if(gyroAngle < lastGyroAngle){
                baseRequestedPosition += (int) (gyroAngle - lastGyroAngle) * CLICK_TO_DEGREE;
            } else {
                
            }
        } else if(mode == TRACKING){
           baseRequestedPosition = (int) (baseAngle + Camera.xAngle) * CLICK_TO_DEGREE;
        } else if(mode == INIT){
            if(zeroTurret()){
                mode = IDLE;
            }
        } else if(mode == IDLE){

        }

        base.set(ControlMode.Position, baseRequestedPosition);
        SmartDashboard.putNumber("Base Position", baseRequestedPosition);
        lastGyroAngle = gyroAngle;
    }

    private static void commandTurret(int input){
        //flywheel.set(ControlMode.Velocity, 3700);
        
    }

    private static void configTalon(TalonSRX thisTalon, boolean inverted){
        thisTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        thisTalon.setSelectedSensorPosition(0);
        thisTalon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10);
        thisTalon.config_kP(0, 0.35);
        thisTalon.config_kI(0, 0.0001);
        thisTalon.config_kD(0, 5.5);
        thisTalon.config_kF(0, 0);
        thisTalon.configMotionAcceleration(500);
        thisTalon.configMotionCruiseVelocity(2000);    
    }

}
