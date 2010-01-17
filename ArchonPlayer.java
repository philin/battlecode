package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class ArchonPlayer extends BasePlayer
{
    public ArchonPlayer(RobotController rc)
    {
        super(rc);
    }

    protected Behavior selectBehavior(Behavior oldBehavior)
    {
        Behavior behavior = new Behavior(Behavior.BehaviorType.MOBILE_CREATE_TERRITORY,
                                         null);
        return behavior;
    }

    protected void mobileCreateTerritory(Object[] state) throws GameActionException
    {
        /*** beginning of main loop ***/
        boolean hasWout = false;
        Robot[] nbrs = myRC.senseNearbyGroundRobots();
        for(Robot r : nbrs)
        {

            RobotInfo info = myRC.senseRobotInfo(r);
            if(info.location.distanceSquaredTo(myRC.getLocation()) <= 2 &&
               info.type == RobotType.WOUT && info.team.equals(myRC.getTeam()))
            {
                hasWout=true;
                double maxTransfer = Math.min(info.maxEnergon -
                                              info.eventualEnergon,1);
                if(maxTransfer < myRC.getEnergonLevel())
                {
                    myRC.transferUnitEnergon(maxTransfer, info.location,
                                             RobotLevel.ON_GROUND);
                }
            }
        }
        MapLocation spawnLoc  = myRC.getLocation().add(myRC.getDirection());
        if(myRC.getEnergonLevel() > RobotType.WOUT.spawnCost() &&
           myRC.senseTerrainTile(spawnLoc).getType() ==
           TerrainTile.TerrainType.LAND &&
           myRC.senseGroundRobotAtLocation(spawnLoc) == null && !hasWout)
        {
            myRC.spawn(RobotType.WOUT);
        }
        else if (!myRC.isMovementActive())
        {
            if (myRC.canMove(myRC.getDirection()))
            {
                System.out.println("about to move");
                myRC.moveForward();
            }
            else
            {
                myRC.setDirection(myRC.getDirection().rotateRight());
            }
        }
    }
}
