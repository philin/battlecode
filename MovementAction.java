package team338;

import battlecode.common.*;

abstract class MovementAction extends Action
{
    public MovementAction(double myPriority)
    {
        super(myPriority);
    }

    public void run(RobotController rc) throws GameActionException
    {
        if(!rc.isMovementActive())
        {
            Direction desiredDir = getNextDirection();
            if(rc.getDirection() != desiredDir)
                rc.setDirection(desiredDir);
            else
                rc.moveForward();
        }
    }

    abstract protected double getBasePriority();
    abstract protected Direction getNextDirection();
}