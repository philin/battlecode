package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class WoutPlayer implements BasePlayer
{
    private final RobotController myRC;
    private final Team team;//safely assume this does not change
    private static final double ARCHON_FIND_THRESHOLD=10;

    public WoutPlayer(RobotController rc)
    {
        myRC = rc;
        team = rc.getTeam();
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
            myRC.setDirection(myRC.getDirection().rotateRight());
        }
    }

    public void run()
    {
        while (true)
        {
            try
            {
                MapLocation loc = myRC.getLocation();
                Direction currDir = myRC.getDirection();
                //if can move
                if (!myRC.isMovementActive()){
                    if(myRC.getEnergonLevel()<ARCHON_FIND_THRESHOLD)
                    {
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
                        if(minArch==null){
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
                    Direction dir = currDir;
                    do
                    {
                        MapLocation l = loc.add(dir);
                        Robot g = myRC.senseGroundRobotAtLocation(loc.add(dir));
                        if (g!=null)
                        {
                            if (myRC.senseRobotInfo(g).team!=team)
                            {
                                //enemy
                                myRC.attackGround(l);
                                break;
                            }
                        }
                        else
                        {
                            Robot a = myRC.senseAirRobotAtLocation(loc.add(dir));
                            if (a!=null)
                            {
                                if (myRC.senseRobotInfo(a).team!=team)
                                {
                                    //enemy
                                    myRC.attackAir(l);
                                    break;
                                }
                            }
                        }
                        dir = dir.rotateRight();
                    }while (dir!=currDir);
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