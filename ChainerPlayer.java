package team338;

import battlecode.common.*;

public class ChainerPlayer extends BasePlayer
{
    public ChainerPlayer(RobotController rc)
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
