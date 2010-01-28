package team338.nav;

import team338.*;
import battlecode.common.*;
import java.util.Random;

//move forward if possible otherwise turn randomly
public class BasicMovement extends MovementAction
{
    Random rand = new Random();
    RobotController rc;
    public BasicMovement(RobotController rc)
    {
        super(1);
        this.rc = rc;
    }
    public boolean isDone()
    {
        return false;
    }
    public double getBasePriority()
    {
        return .5;
    }
    public Direction getNextDirection(RobotState state)
    {
        if(rc.canMove(state.d))
        {
            return state.d;
        }
        else{
            if(rand.nextInt(2)==0)
            {
                Direction dir = state.d.rotateRight();
                while(!rc.canMove(dir)){
                    dir = dir.rotateRight();
                    if(dir==state.d){
                        //careful we can't move but we are still doing stuff
                        return null;
                    }
                }
                return dir;
            }
            else
            {
                Direction dir = state.d.rotateLeft();
                while(!rc.canMove(dir)){
                    dir = dir.rotateLeft();
                    if(dir==state.d){
                        //careful we can't move but we are still doing stuff
                        return null;
                    }
                }
                return dir;
            }
        }
    }

    protected boolean canAct()
    {
        return !rc.isMovementActive();
    }
}
