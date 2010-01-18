package team338;

import battlecode.common.*;

abstract public class BroadcastAction extends Action
{
    public BroadcastAction(double myPriority)
    {
        super(myPriority);
    }

    public void run(RobotState state, RobotController rc) 
        throws GameActionException
    {
        rc.broadcast(getNextMessage(state));
    }

    abstract protected Message getNextMessage(RobotState state)
        throws GameActionException;
}