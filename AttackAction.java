package team338;

import battlecode.common.*;

abstract public class AttackAction extends Action
{
    public AttackAction(double myPriority)
    {
        super(myPriority);
    }

    static class AttackState
    {
        RobotLevel level;
        MapLocation loc;
    }

    public void run(RobotState state, RobotController rc)
        throws GameActionException
    {
        if(!rc.isAttackActive())
        {
            AttackState as = getNextAttackState(state);
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

    abstract protected AttackState getNextAttackState(RobotState state);
}