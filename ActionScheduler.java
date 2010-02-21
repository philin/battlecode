package team338;

import java.util.*;
import battlecode.common.*;

public class ActionScheduler
{

    public ActionScheduler()
    {
        allActions = new ArrayList<Action>();
        backgroundActions = new ArrayList<Action>();
    }

    public void addAction(Action action)
    {
        allActions.add(action);
        current = allActions.iterator();
    }

    public void addBackgroundAction(Action action)
    {
        backgroundActions.add(action);
    }

    public void removeAction(Action action)
    {
        int index = allActions.indexOf(action);
        if(index != -1)
            allActions.remove(index);
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
        for(int i=0;i<numActions();i++)
        {
            toRun = getNextAction();
            if(toRun.canAct()){
                toRun.run(state, rc);
                if(toRun.isDone())
                {
                    current.remove();
                }
                break;
            }
        }
        for(Action action : backgroundActions)
        {
            if(action.canAct()){
                action.run(state, rc);
            }
        }
    }

    private Action getNextAction()
    {
        if(!current.hasNext())
            current = allActions.iterator();
        return current.next();
    }

    private ArrayList<Action> backgroundActions;

    private ArrayList<Action> allActions;
    private Iterator<Action> current;
}