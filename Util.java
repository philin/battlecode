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
            case UnitCommon.BASIC_ATTACKER:
                builder.build(UnitCommon.BASIC_ATTACKER_CHASSIS, loc);
                //add the components
                for (int i = 0; i < UnitCommon.BASIC_ATTACKER_COMPONENTS.length; ++i)
                {
                    addComponent(myRC,
                                 builder,
                                 UnitCommon.BASIC_ATTACKER_COMPONENTS[i],
                                 loc,
                                 UnitCommon.BASIC_ATTACKER_HEIGHT);
                }
                break;
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

            case UnitCommon.BASIC_BUILDING:
                //create the chassis
                builder.build(UnitCommon.BASIC_BUILDING_CHASSIS, loc);
                //add the components
                for (int i = 0; i < UnitCommon.BASIC_BUILDING_COMPONENTS.length; ++i)
                {
                    addComponent(myRC,
                                 builder,
                                 UnitCommon.BASIC_BUILDING_COMPONENTS[i],
                                 loc,
                                 UnitCommon.BASIC_BUILDING_HEIGHT);
                }
                break;
            case UnitCommon.FACTORY:
                builder.build(UnitCommon.FACTORY_CHASSIS, loc);
                //add the components
                for (int i = 0; i < UnitCommon.FACTORY_COMPONENTS.length; ++i)
                {
                    addComponent(myRC,
                                 builder,
                                 UnitCommon.FACTORY_COMPONENTS[i],
                                 loc,
                                 UnitCommon.FACTORY_HEIGHT);
                }
                break;
            default:
                break;
            }

        }
        catch(Exception ex)
        {

            System.out.println(loc.directionTo(myRC.getLocation()));
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
                Planner.doYield();
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

    //returns NORTH for input 0, increases clockwise
    public static Direction intAsDirection(int dir)
    {
        return Direction.values()[dir];
    }
    //included for completeness
    public static int directionAsInt(Direction dir){
        return dir.ordinal();
    }


    public static boolean isFactory(RobotInfo ri)
    {
        ComponentType[] componentList = ri.components;
        for(int i = 0; i < componentList.length; ++i)
        {
            if(componentList[i] == ComponentType.FACTORY)
            {
                return true;
            }

        }

        return false;
    }

    public static boolean isUnarmedFactory(RobotInfo ri)
    {
            boolean hasFactory = false;
        //sight can determine armed-ness because it is the first thing built
        boolean hasSight = false;
        ComponentType[] componentList = ri.components;
        for(int i = 0; i < componentList.length; ++i)
        {
            if(componentList[i] == ComponentType.FACTORY)
            {
                hasFactory = true;
            }
            if(componentList[i] == ComponentType.SIGHT)
            {
                hasSight = true;
            }

        }

        return hasFactory && !hasSight;
    }

    public static boolean isArmedFactory(RobotInfo ri)
    {
        boolean hasFactory = false;
        //sight can determine armed-ness because it is the first thing built
        boolean hasSight = false;
        ComponentType[] componentList = ri.components;
        for(int i = 0; i < componentList.length; ++i)
        {
            if(componentList[i] == ComponentType.FACTORY)
            {
                hasFactory = true;
            }
            if(componentList[i] == ComponentType.SIGHT)
            {
                hasSight = true;
            }

        }

        return hasFactory && hasSight;
    }

}
