package team338;
import battlecode.common.*;

public class SimpleAttack extends AttackAction
{
    RobotController myRC;
    Team team;
    public SimpleAttack(RobotController rc, double myPriority)
    {
        super(myPriority);
        myRC = rc;
        team = myRC.getTeam();
    }

    protected RobotLocation getNextRobotLocation(RobotState state)
        throws GameActionException
    {
        Robot target = null;
        RobotLocation as = new RobotLocation();
        RobotInfo ri= myRC.senseRobotInfo(target);
        as.level = target.getRobotLevel();
        as.loc = ri.location;
        return as;
    }
    protected boolean isDone()
    {
        return false;
    }

    protected double getBasePriority()
    {
        return 1;
    }
    private Robot getTargetInRange() throws GameActionException
    {
        Robot currTarget = null;

        switch(myRC.getRobotType())
        {
            case ARCHON:
                break;
            case AURA:
                break;
            case CHAINER:
                break;
            case COMM:
                break;
            case SOLDIER:
            {
                Direction currDir = myRC.getDirection();
                Direction dir = currDir;
                MapLocation loc = myRC.getLocation();
                int rotCount = 0;
                do
                {

                    currTarget = setTargetPerDirection(currTarget, dir, loc);
                    dir = dir.rotateRight();
                    rotCount++;
                    if(rotCount == 3)
                    {
                        dir = dir.opposite().rotateLeft();
                    }

                }while (dir!=currDir);
                    break;
            }

            case TELEPORTER:
                break;
            case TURRET:
                break;
            case WOUT:
            {
                Direction currDir = myRC.getDirection();
                Direction dir = currDir;
                MapLocation loc = myRC.getLocation();
                do
                {

                    currTarget =  setTargetPerDirection(currTarget, dir, loc);
                    dir = dir.rotateRight();
                }while (dir!=currDir);
                break;
            }

        }
        return currTarget;
    }

    /*
     *Determines the best target given a direction.
     *Sets priority as buildings, archons, then anything else
     */
    public Robot setTargetPerDirection(Robot currTarget, Direction dir,
                                       MapLocation loc)
        throws GameActionException
    {

        MapLocation l = loc.add(dir);
        Robot g = myRC.senseGroundRobotAtLocation(loc.add(dir));
        if (g!=null)
        {
            RobotInfo riGround = myRC.senseRobotInfo(g);
            if (riGround.team!=team)
            {
                //enemy
                if( riGround.type.isBuilding() ||
                    (riGround.type == RobotType.ARCHON &&
                     !riGround.type.isBuilding()) ||
                    currTarget == null)
                {
                    currTarget = g;
                }

            }
        }
        else
        {
            Robot a = myRC.senseAirRobotAtLocation(loc.add(dir));
            RobotInfo riAir = myRC.senseRobotInfo(a);
            if (a!=null)
            {
                if (myRC.senseRobotInfo(a).team!=team)
                {
                    //enemy
                    if(currTarget == null)
                    {
                        currTarget = a;
                    }

                }
            }
        }
        return currTarget;
    }

}

