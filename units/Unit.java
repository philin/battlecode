package team046.units;
import battlecode.common.*;
public abstract class Unit
{

    //basic unit function headers go here

    public abstract int getType();

    public abstract void runBehavior(RobotController myRC);
}