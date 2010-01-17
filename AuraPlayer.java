package team338;

import battlecode.common.*;

public class AuraPlayer extends BuildingPlayer
{
    public AuraPlayer(RobotController rc)
    {
        super(rc);
    }

    public Behavior selectBehavior(Behavior b)
    {
        return b;
    }

    public void runPlayer() throws GameActionException
    {
    }
}