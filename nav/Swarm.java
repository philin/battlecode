package team338.nav;

import battlecode.common.*;
import team338.*;

public class Swarm extends MovementAction
{
    MovementAction backup;
    RobotController rc;
    Team team;
    public Swarm(RobotController rc,MovementAction backup)
    {
        super(1);
        team = rc.getTeam();
        this.rc = rc;
        this.backup = backup;
    }

    public Direction getNextDirection(RobotState state)
        throws GameActionException
    {
        System.out.println("Swarm");
        Robot[] ground = rc.senseNearbyGroundRobots();
        Robot[] air = rc.senseNearbyAirRobots();
        int[] directionCount = {0,0,0,0,0,0,0,0};

        for(Robot r: ground)
        {
            RobotInfo rinfo = rc.senseRobotInfo(r);
            if(rinfo.team==team && !rinfo.type.isBuilding()){
                Direction d =rinfo.directionFacing;
                switch(d)
                {
                    case NORTH:
                        directionCount[0]++;
                        break;
                    case NORTH_EAST:
                        directionCount[1]++;
                        break;
                    case EAST:
                        directionCount[2]++;
                        break;
                    case SOUTH_EAST:
                        directionCount[3]++;
                        break;
                    case SOUTH:
                        directionCount[4]++;
                        break;
                    case SOUTH_WEST:
                        directionCount[5]++;
                        break;
                    case WEST:
                        directionCount[6]++;
                        break;
                    case NORTH_WEST:
                        directionCount[7]++;
                        break;
                }
            }
        }
        for(Robot r: air)
        {
            RobotInfo rinfo = rc.senseRobotInfo(r);
            if(rinfo.team==team){
                Direction d =rinfo.directionFacing;
                switch(d)
                {
                    case NORTH:
                        directionCount[0]+=10000;
                        break;
                    case NORTH_EAST:
                        directionCount[1]+=10000;
                        break;
                    case EAST:
                        directionCount[2]+=10000;
                        break;
                    case SOUTH_EAST:
                        directionCount[3]+=10000;
                        break;
                    case SOUTH:
                        directionCount[4]+=10000;
                        break;
                    case SOUTH_WEST:
                        directionCount[5]+=10000;
                        break;
                    case WEST:
                        directionCount[6]+=10000;
                        break;
                    case NORTH_WEST:
                        directionCount[7]+=10000;
                        break;
                }
            }
        }
        int maxDir=-1;
        int maxCount=0;
        for(int direction=0;direction<8;direction++)
        {
            if(directionCount[direction]>maxCount)
            {
                maxDir = direction;
                maxCount = directionCount[direction];
            }
        }
        Direction ret;
        switch(maxDir)
        {
            case 0:
                ret=Direction.NORTH;
                break;
            case 1:
                ret=Direction.NORTH_EAST;
                break;
            case 2:
                ret=Direction.EAST;
                break;
            case 3:
                ret=Direction.SOUTH_EAST;
                break;
            case 4:
                ret=Direction.SOUTH;
                break;
            case 5:
                ret=Direction.SOUTH_WEST;
                break;
            case 6:
                ret=Direction.WEST;
                break;
            case 7:
                ret=Direction.NORTH_WEST;
                break;
            default:
                ret=backup.getNextDirection(state);
                break;
        }
        if(rc.canMove(ret))
        {
            return ret;
        }
        else
        {
            return null;
        }
    }
    public boolean isDone()
    {
        return true;
    }

    public boolean canAct()
    {
        return !rc.isMovementActive();
    }
    public double getBasePriority()
    {
        return .5;
    }

}
