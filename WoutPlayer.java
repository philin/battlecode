package team338;

import team338.nav.*;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class WoutPlayer extends BasePlayer
{
    private static final double ARCHON_FIND_THRESHOLD=10;

    protected Action exploreAction;
    protected Action returnToArchonAction;
    boolean returning = true;

    public WoutPlayer(RobotController rc)
    {
        super(rc);
        exploreAction = nav.getBasicMovement();
        returnToArchonAction = new Swarm(myRC,nav.getFollowArchon());
        scheduler.addAction(new Swarm(myRC,(MovementAction)exploreAction));
        scheduler.addAction(new GreedyAttackAction(rc, 1.0));
    }

    protected Behavior selectBehavior(Behavior oldBehavior)
    {
        Behavior behavior = new Behavior(Behavior.BehaviorType.WOUT_COLLECT_FLUX,
                                         null);
        return behavior;
    }

    public void tryMoveForward() throws GameActionException
    {
        if(myRC.canMove(myRC.getDirection()))
        {
            System.out.println("about to move");
            myRC.moveForward();
        }
        else
        {
            myRC.setDirection(myRC.getDirection().rotateRight());
        }
    }

    protected void woutCollectFlux(Object[] state) throws GameActionException
    {
        if(myRC.getEnergonLevel()<ARCHON_FIND_THRESHOLD && !returning){
            returnToArchonAction = nav.getFollowArchon();
            scheduler.clearAllActions();
            scheduler.addAction(new SimpleAttackAction(myRC, 1.0));
            scheduler.addAction(returnToArchonAction);
        }
        else if(scheduler.numActions() == 1)
        {
            exploreAction = new Swarm(myRC,nav.getBasicMovement());
            scheduler.addAction(exploreAction);
        }
        /*MapLocation loc = myRC.getLocation();
        Direction currDir = myRC.getDirection();
        MapLocation minArch = null;
        int minDistSquared=-1;
        for(MapLocation arch: myRC.senseAlliedArchons())
        {
            if(minArch==null)
            {
                minArch=arch;
                minDistSquared = arch.distanceSquaredTo(loc);
            }
            else
            {
                int dist = arch.distanceSquaredTo(loc);
                if(dist<minDistSquared){
                    minDistSquared=dist;
                    minArch=arch;
                }
            }
            if(minDistSquared<=1){
                break;
            }
        }
        if(minDistSquared<=2 && minArch != null){
            myRC.transferFlux(myRC.getFlux(),minArch,RobotLevel.IN_AIR);
        }

        //if can move
        if (!myRC.isMovementActive()){
            if(myRC.getEnergonLevel()<ARCHON_FIND_THRESHOLD)
            {
                while(minArch==null){
                    //we've lost
                    myRC.yield();
                }

                Direction dir = loc.directionTo(minArch);
                if(currDir==dir)
                {
                    tryMoveForward();
                }
                else if(dir==Direction.OMNI)
                {
                    //do noting
                }
                else
                {
                    if(myRC.canMove(dir))
                    {
                        myRC.setDirection(dir);
                    }
                    else{
                        tryMoveForward();
                    }
                }
            }
            else
            {
                tryMoveForward();
            }
        }
        else if (!myRC.isAttackActive())
        {
            //do an attack if possible
            Direction dir = currDir;
            do
            {
                MapLocation l = loc.add(dir);
                Robot g = myRC.senseGroundRobotAtLocation(loc.add(dir));
                if (g!=null)
                {
                    if (myRC.senseRobotInfo(g).team!=team)
                    {
                        //enemy
                        myRC.attackGround(l);
                        break;
                    }
                }
                else
                {
                    Robot a = myRC.senseAirRobotAtLocation(loc.add(dir));
                    if (a!=null)
                    {
                        if (myRC.senseRobotInfo(a).team!=team)
                        {
                            //enemy
                            myRC.attackAir(l);
                            break;
                        }
                    }
                }
                dir = dir.rotateRight();
            }while (dir!=currDir);
        }
        */
    }
}