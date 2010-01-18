package team338;

import battlecode.common.*;

abstract public class AttackAction extends Action
{
    public AttackAction(double myPriority)
    {
        super(myPriority);
    }

    public void run(RobotState state, RobotController rc)
        throws GameActionException
    {
        if(!rc.isAttackActive())
        {
            Action.RobotLocation rl = getNextRobotLocation(state);
            switch(as.level)
            {
            case IN_AIR:
                rc.attackAir(as.loc);
                break;
            case ON_GROUND:
                rc.attackGround(as.loc);
                break;
            }
        }
    }

    abstract protected Action.RobotLocation getNextRobotLocation(RobotState state);
}