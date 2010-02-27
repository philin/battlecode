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
    int spawnCounter=0;
    static final int WOUT=4;
    static final int SOLDIER=6;
    static final int CHAINER=7;
    static final int MAX=8;

    public void tryMoveForward() throws GameActionException
    {
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
               info.type != RobotType.ARCHON && info.team.equals(myRC.getTeam()) &&
               !info.type.isBuilding())
            {
                hasWout=true;
                double maxTransfer =
                    Math.min(info.maxEnergon-info.eventualEnergon,
                             myRC.getEnergonLevel()-MIN_ENERGON);
                if(maxTransfer>0)
                {
                    myRC.transferUnitEnergon(maxTransfer, info.location,
                                             RobotLevel.ON_GROUND);
                }
            }
        }

        boolean spawned = false;

        MapLocation spawnLoc  = myRC.getLocation().add(myRC.getDirection());
        if(myRC.getFlux()>3000)
        {
            if(myRC.senseTerrainTile(spawnLoc).getType() == TerrainTile.TerrainType.LAND &&
               myRC.senseGroundRobotAtLocation(spawnLoc) == null)
            {
                myRC.spawn(RobotType.COMM);
                spawned = true;
            }
        }


        RobotType spawnType;
        if(spawnCounter>=CHAINER){
            spawnType = RobotType.CHAINER;
        }
        else if(spawnCounter>=SOLDIER){
            spawnType = RobotType.SOLDIER;
        }
        else{
            spawnType = RobotType.WOUT;
        }
        if(myRC.getEnergonLevel() > MIN_ENERGON+10+spawnType.spawnFluxCost() &&
           myRC.senseTerrainTile(spawnLoc).getType() ==
           TerrainTile.TerrainType.LAND &&
           myRC.senseGroundRobotAtLocation(spawnLoc) == null && !hasWout && !spawned)
        {
            myRC.spawn(spawnType);
            spawnCounter++;
            spawnCounter%=MAX;
        }
        else if (!myRC.isMovementActive() && !spawned)
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
