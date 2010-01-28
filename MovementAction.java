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
            if(desiredDir == null)
                return;
            if(state.d != desiredDir)
                rc.setDirection(desiredDir);
            else
                rc.moveForward();
        }
    }

    abstract public Direction getNextDirection(RobotState state)
        throws GameActionException;
}