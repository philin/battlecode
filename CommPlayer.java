package team338;

import battlecode.common.*;

public class CommPlayer extends BuildingPlayer
{
    public CommPlayer(RobotController rc)
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
