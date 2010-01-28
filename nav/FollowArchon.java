package team338.nav;

import team338.*;
import battlecode.common.*;
import java.util.Random;

public class FollowArchon extends MovementAction
{
    Random rand = new Random();
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
