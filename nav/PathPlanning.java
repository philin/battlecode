package team338.nav;

import team338.*;
import battlecode.common.*;
import java.util.Random;

public class PathPlanning
{
    RobotController rc;
    static Random rand = new Random();



    public PathPlanning(RobotController rc)
    {
        this.rc = rc;
    }

    public MovementAction getFollowArchon()
    {
        return new FollowArchon(rc);
    }

    public MovementAction getBasicMovement()
    {
        return new BasicMovement(rc);
    }
}
