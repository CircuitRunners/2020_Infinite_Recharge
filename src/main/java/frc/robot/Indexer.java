/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Add your docs here.
 */
public class Indexer {
    static boolean balls[] = {false,false,false,false,false};
    static boolean flags[] = {false, false, false, false, false};

    static DigitalInput pos0 = new DigitalInput(1);
    static DigitalInput pos1 = new DigitalInput(2);
    static DigitalInput pos2 = new DigitalInput(3);   
    static DigitalInput pos3 = new DigitalInput(4);
    static DigitalInput pos4 = new DigitalInput(5);
    static DigitalInput waiting = new DigitalInput(6);

    static TalonSRX indexer;
    public static void init(){
        indexer = new TalonSRX(20);
        indexer.setInverted(true);
    }

    public static void run(){
        if(waiting.get()){
            if(!balls[4]){
                flags[4] = true;
            } else if(!balls[3]){
                flags[3] = true;
            } else if(!balls[2]){
                flags[2] = true;
            } else if(!balls[1]){
                flags[1] = true;
            } else if(!balls[0]){
                flags[0] = true;
            }   
        }
        update();
        if(flags[4]){
            indexer.set(ControlMode.PercentOutput, 0.3);
            if(balls[4]){
                flags[4] = false;
                indexer.set(ControlMode.PercentOutput, 0);
            }
        } else if(flags[3]){
            indexer.set(ControlMode.PercentOutput, 0.3);
            if(balls[3]){
                flags[3] = false;
                indexer.set(ControlMode.PercentOutput, 0);
            }         
        } else if(flags[2]){
            indexer.set(ControlMode.PercentOutput, 0.3);
            if(balls[2]){
                flags[2] = false;
                indexer.set(ControlMode.PercentOutput, 0);
            }
        } else if(flags[1]){
            indexer.set(ControlMode.PercentOutput, 0.3);
            if(balls[1]){
                flags[1] = false;
                indexer.set(ControlMode.PercentOutput, 0);
            }
        } else if(flags[0]){
            indexer.set(ControlMode.PercentOutput, 0.3);
            if(balls[0]){
                flags[0] = false;
                indexer.set(ControlMode.PercentOutput, 0);
            }
        }
    }
    static void update(){
        balls[0] = pos0.get();
        balls[1] = pos1.get();
        balls[2] = pos2.get();
        balls[3] = pos3.get();
        balls[4] = pos4.get();
    }
    public static void up(){
        indexer.set(ControlMode.PercentOutput, 0.3);
    }
    public static void down(){
        indexer.set(ControlMode.PercentOutput, -0.3);
    }
    public static void stop(){
        indexer.set(ControlMode.PercentOutput, 0);
    }
}
