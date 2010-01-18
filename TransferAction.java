package team338;

import battlecode.common.*;

abstract public class TransferAction extends Action
{
    enum TransferType { ENERGON, FLUX; }

    public TransferAction(double myPriority)
    {
        super(myPriority);
    }

    static protected class TransferInfo
    {
        TransferType type;
        double amount;
        Action.RobotLocation location;
    }

    public void run(RobotState state, RobotController rc)
        throws GameActionException
    {
        TransferInfo ti = getNextTransfer(state);
        if(ti == null)
            return;
        //RobotInfo ri = ti.location.senseLocation(rc);
        switch(ti.type)
        {
        case ENERGON:
            //double amount = GameConstants.ENERGON_RESERVE_SIZE - ri.energonReserve;
            rc.transferUnitEnergon(ti.amount, ti.location.loc, ti.location.level);
            break;
        case FLUX:
            rc.transferFlux(ti.amount, ti.location.loc, ti.location.level);
            break;
        }
    }

    abstract protected TransferInfo getNextTransfer(RobotState state)
        throws GameActionException;

    private TransferType type;
}