package team338;

import battlecode.common.*;

public class TurretPlayer extends BasePlayer
{
    public TurretPlayer(RobotController rc)
    {
        super(rc);
    }

    public Behavior selectBehavior(Behavior b)
    {
        return b;
    }
}