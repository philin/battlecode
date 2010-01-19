package team338;

import battlecode.common.*;
import java.util.Random;

abstract class BasePlayer implements Runnable
{
    private Behavior oldBehavior;

    protected final RobotController myRC;
    protected final Team team;//safely assume this does not change
    protected static Random r = new Random();

    protected RobotState state;
    protected ActionScheduler scheduler;
    protected team338.nav.PathPlanning nav;

    private BasePlayer()
    {
        System.out.println("needs a RobotController");
        myRC = null;
        team = null;
        oldBehavior = null;

        state = null;
        scheduler = null;
        nav = null;
    }

    public BasePlayer(RobotController rc)
    {
        myRC = rc;
        team = myRC.getTeam();
        oldBehavior = null;

        scheduler = new ActionScheduler();
        nav = new team338.nav.PathPlanning(rc);
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
                    //calculate current state
                    state = new RobotState(myRC.getLocation(),myRC.getDirection());

                    //select desired behavior
                    Behavior behavior = selectBehavior(oldBehavior);

                    //run behavior: select actions
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

                    //perform actions
                    scheduler.run(state, myRC);

                    //cleanup and end turn
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