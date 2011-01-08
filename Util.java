package team046;

import battlecode.common.*;
import team046.units.*;

public class Util
{
    //returns true if loc is occupied,
    //returns false if loc is out of range or if loc is occupied
    public static boolean isOccupied(SensorController sensor, MapLocation loc) throws GameActionException
    {
        return sensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND)!=null;

    }

    /*
      construct a unit of a specified type
      types defined in UnitTypeConstants.java
    */
    public static void buildUnit(RobotController myRC, BuilderController builder, int type, MapLocation loc)
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
                    addComponent(myRC,
                                 builder,
                                 UnitCommon.BASIC_BUILDER_COMPONENTS[i],
                                 loc,
                                 UnitCommon.BASIC_BUILDER_HEIGHT);
                }

                break;
            case UnitCommon.HEAVY_ATTACKER:
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
    public static void addComponent(RobotController myRC, BuilderController builder, ComponentType component, MapLocation loc, RobotLevel level)
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


}
