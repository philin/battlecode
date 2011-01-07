package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.mapping.*;
import team046.nav.*;

public class BasicBuilder extends Unit
{
    public BasicBuilder()
    {
    }

    public int getType()
    {
        return UnitCommon.BASIC_BUILDER;
    }

    public void runBehavior(RobotController myRC)
    {
        ComponentController [] components = myRC.components();
        BuilderController builder = null;
        MovementController motor = null;
        for(int i = 0; i < components.length; ++i)
        {
            if(components[i].type() == ComponentType.CONSTRUCTOR)
            {
                builder = (BuilderController)components[i];
            }
            else if(components[i] instanceof MovementController)
            {
                motor = (MovementController)components[i];
            }
        }
        Map map = new Map(myRC);
        Navigator navigator = new Navigator(myRC,motor,map);
        Random rand = new Random();
        int changeCounter = 0;
        try
        {
            while (true)
            {
                myRC.yield();
                // boolean build = false;
                navigator.doMovement();
                changeCounter++;
                if(changeCounter>10){
                    //choose another location
                    MapLocation newLocation
                        = myRC.getLocation().add(5*(rand.nextInt(10)-4),
                                                 5*(rand.nextInt(10)-4));
                    changeCounter = 0;
                    navigator.setDestination(newLocation);
                }

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
