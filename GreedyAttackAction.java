package team338;
import battlecode.common.*;

public class GreedyAttackAction extends SimpleAttackAction
{
    public GreedyAttackAction(RobotController rc, double myPriority)
    {
        super(rc, myPriority);
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
        RobotInfo currInfo = null;
        double currHP = 0;
        if(currTarget != null)
        {
            currInfo = myRC.senseRobotInfo(currTarget);
            currHP = currInfo.energonLevel;
        }
        if (g!=null)
        {
            RobotInfo riGround = myRC.senseRobotInfo(g);

            if (riGround.team!=team)
            {
                //enemy
 //if no target or new target has less hp
                if(currTarget == null ||
                   riGround.energonLevel < currHP)
                {
                    currTarget = g;
                }

            }
        }
        else
        {
            Robot a = myRC.senseAirRobotAtLocation(loc.add(dir));
            if (a!=null)
            {
                RobotInfo airInfo = myRC.senseRobotInfo(a);
                if (airInfo.team!=team)
                {
                    //enemy
                    if(currTarget == null
                       || airInfo.energonLevel < currHP)
                    {
                        currTarget = a;
                    }

                }
            }
        }
        return currTarget;
    }
}