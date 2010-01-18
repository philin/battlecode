package team338;
import battlecode.common.*;

public class SimpleAttack extends AttackAction
{
    RobotController myRC;
    public SimpleAttack(RobotController rc, double myPriority)
    {
        super(myPriority);
        myRC = rc;
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
        Team team = myRC.getTeam();
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
                break;
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
                    dir = dir.rotateRight();
                }while (dir!=currDir);
            }
            break;
        }
        return currTarget;
    }

}

