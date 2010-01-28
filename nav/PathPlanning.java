package team338.nav;

import team338.*;
import battlecode.common.*;
import java.util.Random;

public class PathPlanning
{
    RobotController rc;
    static Random rand = new Random();

    public static class Swarm extends MovementAction
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

    public static class FollowArchon extends MovementAction
    {
        boolean isTracing=false;
        boolean turnLeft;
        boolean done=false;
        RobotController rc;

        public FollowArchon(RobotController rc)
        {
            super(1);
            this.rc = rc;
        }

        public boolean isDone()
        {
            return done;
        }

        public Direction getNextDirection(RobotState state)
            throws GameActionException
        {
            MapLocation minArch=null;
            double minDistSquared=0;
            for(MapLocation arch: rc.senseAlliedArchons())
            {
                if(minArch==null)
                {
                    minArch=arch;
                    minDistSquared = arch.distanceSquaredTo(state.loc);
                }
                else
                {
                    int dist = arch.distanceSquaredTo(state.loc);
                    if(dist<minDistSquared){
                        minDistSquared=dist;
                        minArch=arch;
                    }
                }
            }
            if(minArch==null){
                done = true;
                return null;
            }
            Direction archDir = state.loc.directionTo(minArch);
            if(archDir==Direction.OMNI)
            {
                done=true;
                return null;
            }
            if(isTracing)
            {
                if(rc.canMove(state.d))
                {
                    Direction d;
                    if(turnLeft)
                    {
                        d = state.d.rotateLeft();
                    }
                    else
                    {
                        d = state.d.rotateRight();
                    }
                    while(rc.canMove(d))
                    {
                        if(d==archDir)
                        {
                            isTracing=false;
                            return d;
                        }
                        else if(turnLeft)
                        {
                            d = d.rotateLeft();
                        }
                        else
                        {
                            d = d.rotateRight();
                        }

                    }
                    if(turnLeft)
                    {
                        return d.rotateRight();
                    }
                    else
                    {
                        return d.rotateLeft();
                    }
                }
                else
                {
                    Direction d;
                    if(turnLeft)
                    {
                        if(rc.canMove(state.d.rotateLeft()))
                        {
                            return state.d.rotateLeft();
                        }
                        d = state.d.rotateRight();
                    }
                    else
                    {
                        if(rc.canMove(state.d.rotateRight()))
                        {
                            return state.d.rotateRight();
                        }
                        d = state.d.rotateLeft();
                    }
                    for(int i=0;i<6;i++)
                    {
                        if(rc.canMove(d)){
                            if(d==archDir){
                                isTracing = false;
                            }
                            return d;
                        }
                        if(turnLeft)
                        {
                            d = d.rotateRight();
                        }
                        else
                        {
                            d = d.rotateLeft();
                        }
                    }
                    //no path out, wait a while
                    return null;
                }
            }
            else if(rc.canMove(archDir))
            {
                return archDir;
            }
            else
            {
                isTracing = true;
                Robot robot
                    = rc.senseGroundRobotAtLocation(state.loc.add(state.d));
                if(robot!=null)
                {
                    RobotInfo info = rc.senseRobotInfo(robot);
                    if(info.type.isBuilding())
                    {
                        //mobile robot, no need to trace
                        isTracing = false;
                    }
                }

                if(rand.nextInt(2)==0)
                {
                    turnLeft=false;
                    return state.d.rotateRight();
                }
                else
                {
                    turnLeft=true;
                    return state.d.rotateLeft();
                }
            }
        }

        public double getBasePriority()
        {
            return 1;
        }

        protected boolean canAct()
        {
            return !rc.isMovementActive();
        }
    }

    //move forward if possible otherwise turn randomly
    public static class BasicMovement extends MovementAction
    {
        RobotController rc;
        public BasicMovement(RobotController rc)
        {
            super(1);
            this.rc = rc;
        }
        public boolean isDone()
        {
            return false;
        }
        public double getBasePriority()
        {
            return .5;
        }
        public Direction getNextDirection(RobotState state)
        {
            if(rc.canMove(state.d))
            {
                return state.d;
            }
            else{
                if(rand.nextInt(2)==0)
                {
                    Direction dir = state.d.rotateRight();
                    while(!rc.canMove(dir)){
                        dir = dir.rotateRight();
                        if(dir==state.d){
                            //careful we can't move but we are still doing stuff
                            return null;
                        }
                    }
                    return dir;
                }
                else
                {
                    Direction dir = state.d.rotateLeft();
                    while(!rc.canMove(dir)){
                        dir = dir.rotateLeft();
                        if(dir==state.d){
                            //careful we can't move but we are still doing stuff
                            return null;
                        }
                    }
                    return dir;
                }
            }
        }

        protected boolean canAct()
        {
            return !rc.isMovementActive();
        }
    }

    public PathPlanning(RobotController rc)
    {
        this.rc = rc;
    }

    public MovementAction getFollowArchon()
    {
        return new FollowArchon(rc);
    }

    public MovementAction getBasicMovement()
    {
        return new BasicMovement(rc);
    }
}
