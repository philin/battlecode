package team338;

import battlecode.common.*;
import java.util.Random;
abstract class BasePlayer implements Runnable
{
    private Behavior oldBehavior;

    protected final RobotController myRC;
    protected MapLocation location;
    protected Direction direction;
    protected final Team team;//safely assume this does not change
    protected static Random r = new Random();

    private BasePlayer()
    {
        System.out.println("needs a RobotController");
        myRC = null;
        team = null;
        oldBehavior = null;
    }

    public BasePlayer(RobotController rc)
    {
        myRC = rc;
        team = myRC.getTeam();
        oldBehavior = null;
    }

    protected abstract Behavior selectBehavior(Behavior oldBehavior);
    /**
     * each behavior function should return before the end of the turn
     * and should not call yield.
     *
     */
    protected void mobileDefendTerritory(Object[] state) throws GameActionException {}
    protected void mobileCreateTerritory(Object[] state) throws GameActionException {}
    protected void mobileAttackUnit(Object[] state) throws GameActionException {}
    protected void woutCollectFlux(Object[] state) throws GameActionException {}

    public final void run()
    {
        while(true)
        {
            try
            {
                while(true)
                {
                    location = myRC.getLocation();
                    direction = myRC.getDirection();
                    Behavior behavior = selectBehavior(oldBehavior);
                    switch(behavior.type)
                    {
                    case MOBILE_DEFEND_TERRITORY:
                        mobileDefendTerritory(behavior.state);
                        break;
                    case MOBILE_CREATE_TERRITORY:
                        mobileCreateTerritory(behavior.state);
                        break;
                    case MOBILE_ATTACK_UNIT:
                        mobileAttackUnit(behavior.state);
                        break;
                    case WOUT_COLLECT_FLUX:
                        woutCollectFlux(behavior.state);
                        break;
                    }
                    oldBehavior = behavior;
                    myRC.yield();
                }
            }
            catch(Throwable t){
                t.printStackTrace();
            }
        }
    }
}