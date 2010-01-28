package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class ArchonPlayer extends BasePlayer
{
    protected double MIN_ENERGON = 35.0;

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
    int spawnSoldierWait=0;
    public void tryMoveForward() throws GameActionException
    {
        if(spawnSoldierWait < 2)
        {
            spawnSoldierWait ++;
            return;
        }
        if(myRC.canMove(myRC.getDirection()))
        {
            System.out.println("about to move");
            myRC.moveForward();
        }
        else
        {
            int dir = r.nextInt()%3;
            switch (dir)
            {
                case (0):
                {
                    myRC.setDirection(myRC.getDirection().rotateRight());
                    break;
                }
                case (1):
                {
                    myRC.setDirection(myRC.getDirection().rotateLeft());
                    break;
                }
                case (2):
                {
                    myRC.setDirection(myRC.getDirection().opposite());
                    break;
                }
            }
        }
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
               info.type != RobotType.ARCHON && info.team.equals(myRC.getTeam()))
            {
                hasWout=true;
                double maxTransfer = Math.min(info.maxEnergon -
                                              info.eventualEnergon,1);
                if(maxTransfer < myRC.getEnergonLevel() &&
                   myRC.getEnergonLevel() > MIN_ENERGON)
                {
                    myRC.transferUnitEnergon(maxTransfer, info.location,
                                             RobotLevel.ON_GROUND);
                }
            }
        }

        MapLocation spawnLoc  = myRC.getLocation().add(myRC.getDirection());
        if(myRC.getEnergonLevel() > MIN_ENERGON &&
           myRC.senseTerrainTile(spawnLoc).getType() ==
           TerrainTile.TerrainType.LAND &&
           myRC.senseGroundRobotAtLocation(spawnLoc) == null && !hasWout)
        {
            if(spawnSoldierWait >= 5)
            {
                myRC.spawn(RobotType.SOLDIER);
                spawnSoldierWait=0;
            }
            else
            {
                myRC.spawn(RobotType.WOUT);
                spawnSoldierWait++;
            }
        }
        else if (!myRC.isMovementActive())
        {
            tryMoveForward();
/*
            if (myRC.canMove(myRC.getDirection()))
            {
                System.out.println("about to move");
                myRC.moveForward();
            }
            else
            {
                myRC.setDirection(myRC.getDirection().rotateRight());
                }*/
        }
    }
}
