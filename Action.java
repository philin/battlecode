package team338;

import battlecode.common.*;

abstract public class Action
{
    public Action(double myPriority)
    {
        priority = myPriority;
    }

    public final double getPriority()
    {
        return priority * getBasePriority();
    }

    static protected class RobotLocation
    {
        RobotLevel level;
        MapLocation loc;
    }

    abstract public void run(RobotState state, RobotController rc)
        throws GameActionException;

    abstract protected double getBasePriority();
    abstract protected boolean isDone();

    private final double priority;
}