package team338;

import battlecode.common.*;

abstract public class MovementAction extends Action
{
    public MovementAction(double myPriority)
    {
        super(myPriority);
    }

    public void run(RobotState state, RobotController rc)
        throws GameActionException
    {
        if(!rc.isMovementActive())
        {
            Direction desiredDir = getNextDirection(state);
            if(state.d != desiredDir)
                rc.setDirection(desiredDir);
            else
                rc.moveForward();
        }
    }

    abstract protected Direction getNextDirection(RobotState state);
}