package team338;

import battlecode.common.*;

public class FluxTransferAction extends Action
{
    MapLocation archonLocation=null;
    RobotController rc;
    public FluxTransferAction(RobotController rc)
    {
        super(1);
        this.rc = rc;
    }

    public boolean isDone()
    {
        return false;
    }

    public void run(RobotState state, RobotController rc) throws GameActionException
    {
        if(archonLocation!=null)
        {
            Robot r = rc.senseAirRobotAtLocation(archonLocation);
            RobotInfo ri = rc.senseRobotInfo(r);
            if(ri.type==RobotType.ARCHON){
                rc.transferFlux(rc.getFlux(),archonLocation,RobotLevel.IN_AIR);
                return;
            }
        }
        MapLocation[] archons = rc.senseAlliedArchons();
        for(MapLocation archon : archons)
        {
            if(archon.isAdjacentTo(state.loc)|| archon.equals(state.loc))
            {
                rc.transferFlux(rc.getFlux(),archon,RobotLevel.IN_AIR);
                return;
            }
        }
    }

    public double getBasePriority()
    {
        return 1;
    }

    public boolean canAct() throws GameActionException
    {
        MapLocation loc = rc.getLocation();
        MapLocation[] archons = rc.senseAlliedArchons();
        for(MapLocation archon : archons)
        {
            if(archon.isAdjacentTo(loc)|| archon.equals(loc))
            {
                archonLocation = archon;
                return rc.getFlux()>0;
            }
        }
        return false;
    }
}