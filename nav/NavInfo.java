package team046.nav;

import team046.mapping.*;
import battlecode.common.*;

public class NavInfo{
    public double f,g,h;
    public int state;
    public int length;
    public Map.Node parent;
    public Direction parentDir;
    private MapLocation oldLocation;
    private MapLocation oldDest;
    public MapLocation location;

    public NavInfo(){
        state=0;
    }

    public void update(MapLocation dest){
        if(dest!=oldDest || location!=oldLocation){
            oldLocation=location;
            oldDest = dest;
            h = Math.abs(dest.x-location.x)+Math.abs(dest.y-location.y);
        }
        f = h+g;
    }
}