package team046;

import java.util.Random;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.units.*;
import team046.mapping.*;
import team046.nav.*;

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


    /*construct a unit of a specified type
      types defined in UnitTypeConstants.java
     */
    public void buildUnit(BuilderController builder, int type)
    {
        switch (type)
        {
        case UnitTypeConstants.BASIC_BUILDER:
            break;
        case UnitTypeConstants.HEAVY_ATTACKER:
            break;
        default:
            break;
        }
    }

    //test function, replace soon
    public void runMotor(MovementController motor)
    {
        Map map = new Map(myRC);
        Navigator navigator = new Navigator(myRC,motor,map);
        Random rand = new Random();
        int changeCounter = 0;
        while(true){

            myRC.yield();
            navigator.doMovement();
            changeCounter++;
            if(changeCounter>10){
                //choose another location
                MapLocation newLocation
                    = myRC.getLocation().add(rand.nextInt(10)-4,
                                             rand.nextInt(10)-4);
                changeCounter = 0;
                navigator.setDestination(newLocation);
            }
        }
    }
}