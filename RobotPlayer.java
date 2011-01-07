package team046;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.units.*;

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
                        System.out.println("built component!!!!!!!!!!!!!");
                        break;
                    }
                }
                break;
            case UnitTypeConstants.HEAVY_ATTACKER:
                break;
            default:
                break;
            }
            System.out.println("built!!!");
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
        for(int i = 0; i < components.length; ++i)
        {
            if(components[i].type() == ComponentType.RECYCLER)
            {
                builder = (BuilderController)components[i];
            }
        }
        while(true)
        {
            System.out.println(myRC.getTeamResources());
            if(myRC.getTeamResources() > 50)
            {

                buildUnit(builder,
                          UnitTypeConstants.BASIC_BUILDER,
                          myRC.getLocation().add(Direction.SOUTH));

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
        System.out.println("identified basic builder");
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