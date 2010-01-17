package team338;

import battlecode.common.*;

public abstract class BuildingPlayer extends BasePlayer
{
    private BuildingPlayer()
    {
        super(null);
        System.out.println("needs a RobotController");
    }

    public BuildingPlayer(RobotController rc)
    {
        super(rc);
    }
}