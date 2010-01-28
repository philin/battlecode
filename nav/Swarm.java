package team338.nav;

import battlecode.common.*;
import team338.*;
import java.util.Random;

public class Swarm extends MovementAction
{
    Random rand = new Random();
    //the
    public static final int DIRECTION_MIN_DISTSQUARED=2;
    public static final int DIRECTION_MAX_DISTSQUARED=16;
    //how important is which direction the robot is from us
    public static final int DIRECTION_WEIGHT=1;
    //how important is the direction other robots are facing
    public static final int HEADING_WEIGHT=1;
    public static final int ARCHON_WEIGHT=1000;

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
        Robot[] ground = rc.senseNearbyGroundRobots();
        Robot[] air = rc.senseNearbyAirRobots();
        int[] directionCount = {0,0,0,0,0,0,0,0};
        MapLocation loc = rc.getLocation();

        for(Robot r: ground)
        {
            RobotInfo rinfo = rc.senseRobotInfo(r);
            if(rinfo.team==team && rinfo.type == RobotType.WOUT)
            {
                Direction d;
                int distance = loc.distanceSquaredTo(rinfo.location);
                if(distance<DIRECTION_MAX_DISTSQUARED)
                {
                    if(distance>DIRECTION_MIN_DISTSQUARED)
                    {
                        d = loc.directionTo(rinfo.location);
                        directionCount[d.ordinal()]+=DIRECTION_WEIGHT;
                    }
                    else{
                        d =rinfo.directionFacing;
                        directionCount[d.ordinal()]+=HEADING_WEIGHT;
                    }
                }
            }
        }
        for(Robot r: air)
        {
            RobotInfo rinfo = rc.senseRobotInfo(r);
            if(rinfo.team==team){
                Direction d;
                int distance = loc.distanceSquaredTo(rinfo.location);
                if(distance<DIRECTION_MAX_DISTSQUARED)
                {
                    if(distance>DIRECTION_MIN_DISTSQUARED)
                    {
                        d = loc.directionTo(rinfo.location);
                        directionCount[d.ordinal()]+=DIRECTION_WEIGHT*ARCHON_WEIGHT;
                    }
                    else{
                        d =rinfo.directionFacing;
                        directionCount[d.ordinal()]+=HEADING_WEIGHT*ARCHON_WEIGHT;
                    }
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
        if(ret==null)
        {
            return null;
        }
        if(rc.canMove(ret))
        {
            return ret;
        }
        else if(rc.canMove(state.d))
        {
            return state.d;
        }
        else if(state.d!=ret)
        {
            return ret;
        }
        else
        {
            if(rand.nextInt(2)==0){
                Direction dir = ret.rotateRight();
                while(!rc.canMove(dir) && dir!=ret){
                    dir = dir.rotateRight();
                }
                if(dir==ret){
                    return null;
                }
                return dir;
            }
            else{
                Direction dir = ret.rotateLeft();
                while(!rc.canMove(dir) && dir!=ret){
                    dir = dir.rotateLeft();
                }
                if(dir==ret){
                    return null;
                }
                return dir;
            }
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
