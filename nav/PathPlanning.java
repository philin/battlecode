package team338.nav;

import team338.*;
import battlecode.common.*;
import java.util.Random;

public class PathPlanning
{
    RobotController rc;
    static Random rand = new Random();
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
                    while(rc.canMove(d))
                    {
                        if(d==archDir)
                        {
                            isTracing=false;
                            return d;
                        }
                        else if(turnLeft)
                        {
                            d = d.rotateRight();
                        }
                        else
                        {
                            d = d.rotateLeft();
                        }
                    }
                    if(turnLeft)
                    {
                        return d.rotateLeft();
                    }
                    else
                    {
                        return d.rotateRight();
                    }
                }
            }
            else if(rc.canMove(archDir))
            {
                return archDir;
            }
            else if(rand.nextInt(2)==0)
            {
                isTracing=true;
                turnLeft=false;
                return state.d.rotateRight();
            }
            else
            {
                isTracing=true;
                turnLeft=true;
                return state.d.rotateLeft();
            }
        }

        public double getBasePriority()
        {
            return 1;
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
                    }
                    return dir;
                }
                else
                {
                    Direction dir = state.d.rotateLeft();
                    while(!rc.canMove(dir)){
                        dir = dir.rotateLeft();
                    }
                    return dir;
                }

            }
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