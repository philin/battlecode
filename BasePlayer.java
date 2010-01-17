package team338;

import battlecode.common.*;

abstract class BasePlayer implements Runnable
{
    private Behavior oldBehavior;

    protected final RobotController myRC;
    protected MapLocation location;
    protected Direction direction;
    protected final Team team;//safely assume this does not change
    
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

    protected abstract Behavior SelectBehavior(Behavior oldBehavior);
    /**
     * each behavior function should return before the end of the turn 
     * and should not call yield.
     *
     */
    protected void MobileDefendTerritory(Object[] state) throws GameActionException {}
    protected void MobileCreateTerritory(Object[] state) throws GameActionException {}
    protected void MobileAttackUnit(Object[] state) throws GameActionException {}
    protected void WoutCollectFlux(Object[] state) throws GameActionException {}

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
                    Behavior behavior = SelectBehavior(oldBehavior);
                    switch(behavior.type)
                    {
                    case MOBILE_DEFEND_TERRITORY:
                        MobileDefendTerritory(behavior.state);
                        break;
                    case MOBILE_CREATE_TERRITORY:
                        MobileCreateTerritory(behavior.state);
                        break;
                    case MOBILE_ATTACK_UNIT:
                        MobileAttackUnit(behavior.state);
                        break;
                    case WOUT_COLLECT_FLUX:
                        WoutCollectFlux(behavior.state);
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