package team046;

import battlecode.common.*;

public class Util
{
    //returns true if loc is occupied,
    //returns false if loc is out of range or if loc is occupied
    public static boolean isOccupied(SensorController sensor, MapLocation loc) throws GameActionException
    {
        return sensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND)!=null;

    }

}
