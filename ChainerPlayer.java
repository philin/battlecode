package team338;

import battlecode.common.*;

public class ChainerPlayer extends BasePlayer
{
    private static final double ARCHON_FIND_THRESHOLD=30;

    protected Action exploreAction;
    protected Action returnToArchonAction;

    public ChainerPlayer(RobotController rc)
    {
        super(rc);
        exploreAction = nav.getBasicMovement();
        returnToArchonAction = nav.getFollowArchon();
        // scheduler.addActionnew Swarm(myRC,(MovementAction)exploreAction));
        scheduler.addAction(new GreedyAttackAction(rc, 1.0));


    }

    public Behavior selectBehavior(Behavior b)
    {
        Behavior behavior = new Behavior(Behavior.BehaviorType.MOBILE_ATTACK_UNIT,
                                         null);
        return behavior;
    }

    protected void mobileAttackUnit(Object[] state) throws GameActionException
    {
        if(scheduler.numActions() == 1)
        {
            scheduler.addAction(nav.getFollowArchon());
        }
    }
}
