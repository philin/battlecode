package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class WoutPlayer implements BasePlayer
{
    private final RobotController myRC;
    private static final double ARCHON_FIND_THRESHOLD=10;

    public WoutPlayer(RobotController rc)
    {
        myRC = rc;
    }

    public void tryMoveForward() throws GameActionException
    {
        if(myRC.canMove(myRC.getDirection()))
        {
            System.out.println("about to move");
            myRC.moveForward();
        }
        else{
            myRC.setDirection(myRC.getDirection().rotateRight());
        }
    }

    public void run()
    {
        while (true)
        {
            try
            {
                /*** beginning of main loop ***/
                while (myRC.isMovementActive())
                {
                    myRC.yield();
                }
                if(myRC.getEnergonLevel()<ARCHON_FIND_THRESHOLD)
                {
                    MapLocation loc = myRC.getLocation();
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
                    }
                    Direction currDir = myRC.getDirection();
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
                myRC.yield();

                /*** end of main loop ***/
            }
            catch (Exception e)
            {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}