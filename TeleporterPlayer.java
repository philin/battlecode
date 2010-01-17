package team338;

import battlecode.common.*;

public class TeleporterPlayer extends BuildingPlayer
{
    public TeleporterPlayer(RobotController rc)
    {
        super(rc);
    }

    public Behavior selectBehavior(Behavior b)
    {
        return b;
    }
}
