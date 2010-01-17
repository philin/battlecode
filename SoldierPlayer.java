package team338;

import battlecode.common.*;

public class SoldierPlayer extends BasePlayer
{
    public SoldierPlayer(RobotController rc)
    {
        super(rc);
    }

    public Behavior selectBehavior(Behavior b)
    {
        return b;
    }
}
