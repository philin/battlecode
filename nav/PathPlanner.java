package team046.nav;

import battlecode.common.*;

public interface PathPlanner{
    public Direction[] planPath(MapLocation dest);
    public void setDest(MapLocation dest);
    public void doPlanning(int minSteps);
    public Direction[] getPath();
    public void didTurn(Direction dir);
    public void didMove(Direction dir);
    public void gotStuck(int index);
}