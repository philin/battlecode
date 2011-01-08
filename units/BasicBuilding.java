package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;
import team046.*;

public class BasicBuilding extends Unit
{
    public BasicBuilding()
    {
    }

    public int getType()
    {
        return UnitCommon.BASIC_BUILDING;
    }

    public void runBehavior(RobotController myRC)
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
            if(myRC.getTeamResources() > 100)
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
            myRC.yield();
        }

    }
}