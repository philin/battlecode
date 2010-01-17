package team338;

import battlecode.common.*;

abstract class BasePlayer implements Runnable
{
    protected final RobotController myRC;
    protected MapLocation location;
    protected Direction direction;
    protected final Team team;//safely assume this does not change

    private BasePlayer()
    {
        System.out.println("needs a RobotController");
        myRC = null;
        team = null;
    }

    public BasePlayer(RobotController rc)
    {
        myRC = rc;
        team = myRC.getTeam();
    }

    /**
     *runPlayer should return before the end of the turn and should not call yield
     *
     */
    public abstract void runPlayer() throws GameActionException;

    public void run()
    {
        while(true)
        {
            try
            {
                while(true)
                {
                    location = myRC.getLocation();
                    direction = myRC.getDirection();
                    runPlayer();
                    myRC.yield();
                }
            }
            catch(Throwable t){
                t.printStackTrace();
            }
        }
    }
}