package team338;

import battlecode.common.*;

abstract public class SimpleBroadcastAction extends Action
{
    public SimpleBroadcastAction(double myPriority, Message myMsg)
    {
        super(myPriority);
        msg = myMsg;
    }

    protected Message getNextMessage(RobotState state)
    {
        return msg;
    }

    protected double getBasePriority()
    {
        return 1.0;
    }

    protected boolean isDone()
    {
        return true;
    }

    protected final Message msg;
}