package team338;

import java.util.*;
import battlecode.common.*;

public class ActionScheduler
{
    public ActionScheduler()
    {
        allActions = new ArrayList<Action>();
    }

    public void addAction(Action action)
    {
        allActions.add(action);
        current = allActions.iterator();
    }

    public void clearAllActions()
    {
        allActions.clear();
    }

    public int numActions()
    {
        return allActions.size();
    }

    public void run(RobotState state, RobotController rc)
        throws GameActionException
    {
        if(allActions.size() == 0)
            return;
        Action toRun;
        do
        {
            toRun = getNextAction();
        } while(!toRun.canAct());
        toRun.run(state, rc);
        if(toRun.isDone())
        {
            current.remove();
        }
    }

    private Action getNextAction()
    {
        if(!current.hasNext())
            current = allActions.iterator();
        return current.next();
    }

    private ArrayList<Action> allActions;
    private Iterator<Action> current;
}