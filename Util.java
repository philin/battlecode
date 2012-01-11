package team048;

import battlecode.common.*;

public class Util
{
    //returns true if loc is occupied,
    //returns false if loc is out of range or if loc is occupied
    public static boolean isOccupied(RobotController rc, MapLocation loc) throws GameActionException
    {
        return rc.senseObjectAtLocation(loc, RobotLevel.ON_GROUND)!=null;

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
}
