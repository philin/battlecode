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
            if(rl == null)
                return;
            switch(rl.level)
            {
            case IN_AIR:
                rc.attackAir(rl.loc);
                break;
            case ON_GROUND:
                rc.attackGround(rl.loc);
                break;
            }
        }
    }

    abstract protected RobotLocation getNextRobotLocation(RobotState state)
        throws GameActionException;
}