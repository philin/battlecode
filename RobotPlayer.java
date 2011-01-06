package team046;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable
{
    private final RobotController myRC;

    public RobotPlayer(RobotController rc)
    {
        myRC = rc;
    }

    public void run()
    {
        ComponentController [] components = myRC.newComponents();
        if(myRC.getChassis() != Chassis.BUILDING)
            runMotor((MovementController)components[0]);
    }


    public void runMotor(MovementController motor)
    {

        while (true)
        {
            try
            {
                /*** beginning of main loop ***/
                while (motor.isActive())
                {
                    myRC.yield();
                }

                if (motor.canMove(myRC.getDirection()))
                {
                    //System.out.println("about to move");
                    motor.moveForward();
                }
                else
                {
                    motor.setDirection(myRC.getDirection().rotateRight());
                }

                /*** end of main loop ***/
            }
            catch (Exception e)
            {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }


}