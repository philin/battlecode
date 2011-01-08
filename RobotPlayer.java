package team046;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.units.*;
import team046.mapping.*;
import team046.nav.*;

public class RobotPlayer implements Runnable
{
    private final RobotController myRC;

    private Unit unit;
    public RobotPlayer(RobotController rc)
    {
        myRC = rc;
    }

    public void run()
    {
        System.out.println(myRC.getTeamResources());

        initiateUnitType();


    }

    /*
      robots must wait in this function until they have enough modules to fall into a
      unit category
    */
    public void initiateUnitType()
    {
        while (true)
        {
            ComponentController [] components = myRC.components();
            if (myRC.getChassis() == Chassis.BUILDING)
            {
                boolean hasSensor = false;
                boolean hasBuilder = false;
                for(int i = 0; i < components.length; ++i)
                {
                    if(components[i].type() == ComponentType.RECYCLER)
                    {
                        hasSensor = true;
                    }
                    else if(components[i] instanceof SensorController)
                    {
                        hasBuilder = true;
                    }
                }
                if(hasSensor && hasBuilder)
                {
                    System.out.println("initiating BASIC_BUILDING");
                    Unit basicBuilding = new BasicBuilding();
                    this.unit = basicBuilding;
                    this.unit.runBehavior(this.myRC);

                }

            }
            else
            {

                boolean hasSensor = false;
                boolean hasBuilder = false;
                for(int i = 0; i < components.length; ++i)
                {
                    if(components[i].type() == ComponentType.CONSTRUCTOR)
                    {
                        hasSensor = true;
                    }
                    else if(components[i].type() == ComponentType.SIGHT)
                    {
                        hasBuilder = true;
                    }
                }
                if(hasSensor && hasBuilder)
                {
                     System.out.println("initiating BASIC_BUILDER");
                     Unit basicBuilder = new BasicBuilder();
                     this.unit = basicBuilder;
                     basicBuilder.runBehavior(myRC);
                }
            }
            myRC.yield();
        }

    }

}