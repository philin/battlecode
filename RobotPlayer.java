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
                    runBasicBuilder();
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
            case UnitTypeConstants.BASIC_BUILDER:
                //create the chassis
                builder.build(Chassis.LIGHT, loc);
                //add the components
                while (true)
                {
                    //vait for cooldown and resources
                    if(builder.isActive() ||
                       myRC.getTeamResources() < ComponentType.CONSTRUCTOR.cost)
                    {
                        myRC.yield();
                    }
                    else
                    {
                        builder.build(ComponentType.CONSTRUCTOR,
                                      loc,
                                      RobotLevel.ON_GROUND);
                        break;
                    }
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


    /*
      this function defines the behavioral sequence for a BASIC_BUILDER
    */
    public void runBasicBuilder()
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
                boolean build = false;


                if(myRC.getTeamResources() > 50)
                {
                    build = true;

                }
                if(!build)
                {
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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}