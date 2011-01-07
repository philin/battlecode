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

    private List<Unit> units;
    public RobotPlayer(RobotController rc)
    {
        units = new ArrayList<Unit>();
        myRC = rc;
    }

    public void run()
    {
        System.out.println(myRC.getTeamResources());

        if(myRC.getChassis() != Chassis.BUILDING)
        {
            initiateRobotType();

        }
        else
        {
            runBuilding();
        }

    }


    /*
      robots must wait in this function until they have enough modules to fall into a
      unit category
    */
    public void initiateRobotType()
    {
        while (true)
        {
            ComponentController [] components = myRC.components();
            for(int i = 0; i < components.length; ++i)
            {
                if(components[i].type() == ComponentType.CONSTRUCTOR)
                {
                    System.out.println("initiating BASIC_BUILDER");
                    Unit basicBuilder = new BasicBuilder();
                    units.add(basicBuilder);
                    basicBuilder.runBehavior(myRC);
                }
            }

            myRC.yield();
        }

    }
    /*
      construct a unit of a specified type
      types defined in UnitTypeConstants.java
    */
    public void buildUnit(BuilderController builder, int type, MapLocation loc)
    {
        try{
            if(builder.isActive())
            {
                return;
            }
            switch (type)
            {
            case UnitCommon.BASIC_BUILDER:
                //create the chassis
                builder.build(UnitCommon.BASIC_BUILDER_CHASSIS, loc);
                //add the components
                for (int i = 0; i < UnitCommon.BASIC_BUILDER_COMPONENTS.length; ++i)
                {
                    addComponent(builder,
                                 UnitCommon.BASIC_BUILDER_COMPONENTS[i],
                                 loc,
                                 UnitCommon.BASIC_BUILDER_HEIGHT);
                }

                break;
            case UnitTypeConstants.HEAVY_ATTACKER:
                break;
            default:
                break;
            }
            System.out.println("BASIC_BUILDER built");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*
      this function handles the component adding wait loop
     */
    private void addComponent(BuilderController builder, ComponentType component, MapLocation loc, RobotLevel level)
        throws GameActionException
    {

        while (true)
        {
            //vait for cooldown and resources
            if(builder.isActive() ||
               myRC.getTeamResources() < component.cost)
            {
                myRC.yield();
            }
            else
            {
                builder.build(component,
                              loc,
                              level);
                break;
            }
        }

    }

    /*
      this function runs the behavioral sequence for the starting buildings
    */
    public void runBuilding()
    {
        ComponentController [] components = myRC.components();
        BuilderController builder = null;
        SensorController sensor = null;
        for(int i = 0; i < components.length; ++i)
        {
            if (components[i].type() == ComponentType.RECYCLER)
            {
                builder = (BuilderController)components[i];
            }
            else if (components[i]  instanceof SensorController)
            {
                sensor = (SensorController)components[i];

            }
        }
        while(true)
        {
            if(myRC.getTeamResources() > 50)
            {
                MapLocation loc = myRC.getLocation();
                try{
                    //find the first available tile
                    if(!Util.isOccupied(sensor, loc.add(Direction.NORTH)))
                    {
                        loc = loc.add(Direction.NORTH);
                    }
                    else if(!Util.isOccupied(sensor, loc.add(Direction.EAST)))
                    {
                        loc = loc.add(Direction.EAST);
                    }
                    else if(!Util.isOccupied(sensor, loc.add(Direction.SOUTH)))
                    {
                        loc = loc.add(Direction.SOUTH);
                    }
                    else if(!Util.isOccupied(sensor, loc.add(Direction.WEST)))
                    {
                        loc = loc.add(Direction.WEST);
                    }
                    else
                    {
                        loc = null;
                    }
                    if (loc != null)
                    {
                        buildUnit(builder,
                                  UnitTypeConstants.BASIC_BUILDER,
                                  loc);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            myRC.yield();
        }
    }



}