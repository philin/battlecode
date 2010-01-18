package team338;

import battlecode.common.*;

public class RobotState
{
    final MapLocation loc;
    final Direction d;

    public RobotState()
    {
        loc=null;
        d=null;
    }

    public RobotState(MapLocation loc, Direction d)
    {
        this.loc = loc;
        this.d = d;
    }
}