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
                boolean hasConstructor = false;
                boolean hasFactory = false;
                for(int i = 0; i < components.length; ++i)
                {
                    if(components[i].type() == ComponentType.RECYCLER)
                    {
                        hasSensor = true;
                    }
                    else if(components[i] instanceof SensorController)
                    {
                        hasConstructor = true;
                    }
                    else if(components[i].type() == ComponentType.FACTORY)
                    {
                        hasFactory = true;
                    }
                }
                if(hasFactory)
                {
                    Unit factory = new Factory(myRC);
                    this.unit = factory;
                    factory.runBehavior();
                }
                if(hasSensor && hasConstructor)
                {

                    Unit basicBuilding = new BasicBuilding(myRC);
                    this.unit = basicBuilding;
                    this.unit.runBehavior();

                }

            }
            else
            {

                boolean hasSight = false;
                boolean hasRadar = false;
                boolean hasConstructor = false;
                boolean hasBlaster = false;

                for(int i = 0; i < components.length; ++i)
                {
                    if(components[i].type() == ComponentType.CONSTRUCTOR)
                    {
                        hasConstructor = true;
                    }
                    else if(components[i].type() == ComponentType.SIGHT)
                    {
                        hasSight = true;
                    }
                    else if(components[i].type() == ComponentType.BLASTER)
                    {
                        hasBlaster = true;
                    }
                    else if(components[i].type() == ComponentType.RADAR)
                    {
                        hasRadar = true;
                    }

                }

                if(hasSight && hasConstructor && !hasBlaster)
                {
                     Unit basicBuilder = new BasicBuilder(myRC);
                     this.unit = basicBuilder;
                     basicBuilder.runBehavior();
                }
                if(hasRadar && hasBlaster && !hasConstructor)
                {
                    Unit basicAttacker = new BasicAttacker(myRC);
                    this.unit = basicAttacker;
                    basicAttacker.runBehavior();
                }
            }
            //we cannot replace this yield with doYield because planner is not
            //ready
            myRC.yield();
        }

    }

}