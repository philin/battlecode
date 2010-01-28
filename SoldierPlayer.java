package team338;
import team338.nav.*;
import battlecode.common.*;
import java.util.Random;
import static battlecode.common.GameConstants.*;
public class SoldierPlayer extends BasePlayer
{
    private static final double ARCHON_FIND_THRESHOLD=30;

    protected Action exploreAction;
    protected Action returnToArchonAction;


    boolean returning = true;
    public SoldierPlayer(RobotController rc)
    {
        super(rc);
        exploreAction = nav.getBasicMovement();
        returnToArchonAction = new Swarm(myRC,nav.getFollowArchon());
        // scheduler.addAction(new Swarm(myRC,(MovementAction)exploreAction));
        scheduler.addAction(new GreedyAttackAction(rc, 1.0));

    }

    public Behavior selectBehavior(Behavior b)
    {
        Behavior behavior = new Behavior(Behavior.BehaviorType.MOBILE_ATTACK_UNIT,
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

    protected void mobileAttackUnit(Object[] state) throws GameActionException
    {
        if(myRC.getEnergonLevel()<ARCHON_FIND_THRESHOLD && !returning){
            returnToArchonAction = nav.getFollowArchon();
            scheduler.clearAllActions();
            scheduler.addAction(new GreedyAttackAction(myRC, 1.0));
            scheduler.addAction(returnToArchonAction);
        }

        else if(scheduler.numActions() == 1)
        {
            exploreAction = new Swarm(myRC,nav.getBasicMovement());
            scheduler.addAction(exploreAction);
            //scheduler.addAction(new GreedyAttackAction(myRC, 1.0));
            // scheduler.addAction(returnToArchonAction);
        }

        /* MapLocation loc = myRC.getLocation();
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
            if(myRC.getEnergonLevel()<10)
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
            Direction dir = currDir.rotateLeft().rotateLeft();
            Robot currTarget = null;
            RobotInfo currRi = null;
            for(int i=0;i<5;i++)
            {
                MapLocation l = loc.add(dir);
                Robot g = myRC.senseGroundRobotAtLocation(loc.add(dir));
                if (g!= null)
                {
                    RobotInfo ri = myRC.senseRobotInfo(g);
                    if (ri.team!=team)
                    {
                        if(currTarget == null || ri.type.isBuilding())
                        {
                            currTarget = g;
                            currRi = ri;
                        }
                    }
                }
                Robot a = myRC.senseAirRobotAtLocation(loc.add(dir));
                if(a!=null){
                    RobotInfo airRi =  myRC.senseRobotInfo(a);
                    if(airRi.team != team)
                    {

                        //enemy
                        if(currTarget == null)
                        {
                            currTarget = a;
                            currRi = airRi;
                        }
                    }
                }
                dir = dir.rotateRight();
            };

            if(currTarget != null)
                if(currRi.type.isAirborne())
                {
                    myRC.attackAir(currRi.location);
                }
                else
                {
                    myRC.attackGround(currRi.location);

                }

        }
        */
    }

}




