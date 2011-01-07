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
    /*construct a unit of a specified type
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
                    if(builder.isActive() || myRC.getTeamResources() < ComponentType.CONSTRUCTOR.cost)
                    {
                        myRC.yield();
                    }
                    else
                    {
                        builder.build(ComponentType.CONSTRUCTOR, loc, RobotLevel.ON_GROUND);
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
        try
        {
            while (true)
            {
                boolean build = false;


                if(myRC.getTeamResources() > 50)
                {
                    build = true;

                }
                if(!build)
                {

                    if(motor.isActive())
                    {
                        myRC.yield();
                    }
                    else if (motor.canMove(myRC.getDirection()))
                    {
                        //System.out.println("about to move");
                        motor.moveForward();
                    }
                    else
                    {
                        motor.setDirection(myRC.getDirection().rotateRight());
                    }


                }
                myRC.yield();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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