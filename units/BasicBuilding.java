package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;
import team046.*;

public class BasicBuilding extends Unit
{
    BuilderController builder = null;
    SensorController sensor = null;
    SensorController radarsensor = null;
    WeaponController weapon = null;
    MovementController motor = null;
    public BasicBuilding(RobotController myRC)
    {
        super(myRC);
        ComponentController [] components = myRC.components();
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

    }

    public int getType()
    {
        return UnitCommon.BASIC_BUILDING;
    }

    private void armSelf()
    {
        try{
            Util.addComponent(myRC,
                              builder,
                              ComponentType.RADAR,
                              myRC.getLocation(),
                              RobotLevel.ON_GROUND);
            myRC.yield();
            Util.addComponent(myRC,
                              builder,
                              ComponentType.BLASTER,
                              myRC.getLocation(),
                              RobotLevel.ON_GROUND);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        for(ComponentController comp : myRC.components())
        {
            if(comp.type() == ComponentType.RADAR)
            {
                radarsensor = (SensorController)comp;
            }
            if(comp.type() == ComponentType.BLASTER)
            {
                weapon = (WeaponController)comp;
            }
            if(comp instanceof MovementController)
            {
                motor = (MovementController)comp;
            }
        }
    }

    public void runBehavior()
    {
        armSelf();
        while(true)
        {
            myRC.yield();
            if(myRC.getTeamResources() > 250 )
            {
                try{
                    motor.setDirection(Direction.WEST);
                }
                catch(Exception ex)
                {
                }
                MapLocation loc = myRC.getLocation();
                try{
                    //find the first available tile

                    if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                        loc.add(Direction.NORTH)))
                    {
                        loc = loc.add(Direction.NORTH);
                    }
                    else if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                                loc.add(Direction.EAST)))
                    {
                        loc = loc.add(Direction.EAST);
                    }
                    else if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                             loc.add(Direction.WEST)))
                    {
                        loc = loc.add(Direction.WEST);
                    }
                    else if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                             loc.add(Direction.SOUTH)))
                    {
                        loc = loc.add(Direction.SOUTH);
                    }
                    else
                    {
                        loc = null;
                    }
                    if (loc != null)
                    {
                        Util.buildUnit(myRC,
                                       builder,
                                       UnitCommon.BASIC_BUILDER,
                                       loc);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

        }

    }
}