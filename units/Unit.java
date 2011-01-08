package team046.units;
import battlecode.common.*;
import team046.*;
public abstract class Unit extends Planner
{

    protected RobotController myRC;
    public Unit(RobotController myRC){
        this.myRC = myRC;
    }
    //basic unit function headers go here

    public abstract int getType();

    public abstract void runBehavior();
}