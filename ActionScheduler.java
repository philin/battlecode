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

    public void run(RobotState state, RobotController rc)
        throws GameActionException
    {
        if(allActions.size() == 0)
            return;
        Action toRun;
        if(!current.hasNext())
            current = allActions.iterator();
        toRun = current.next();
        toRun.run(state, rc);
        if(toRun.isDone())
        {
            current.remove();
        }
    }

    private ArrayList<Action> allActions;
    Iterator<Action> current;
}