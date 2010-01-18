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

    abstract public void run(RobotController rc) throws GameActionException;
    abstract protected double getBasePriority();
    abstract protected boolean isDone();

    private final double priority;
}