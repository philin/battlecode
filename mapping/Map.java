package team046.mapping;

import battlecode.common.*;

class Map{
    public class LocationInfo{
        public TerrainTile terrain;
        public GameObject mine;
        public GameObject[] robots;
        public int updateRound;
    };
    private LocationInfo[][] map;
    private int size;
    private RobotController rc;
    static final int MAX_SIZE = 70;
    public Map(RobotController rc){
        this(rc, MAX_SIZE);
    }
    //use this version if we know the size of the map
    public Map(RobotController rc, int size){
        this.rc = rc;
        map = new LocationInfo[size][];
        for(int i=0;i<size;i++){
            map[i] = new LocationInfo[size];
        }
    }

    public void setTerrain(int x, int y, TerrainTile tile){
        map[x][y].terrain = tile;
    }

    public TerrainTile getTerrain(int x, int y){
        if(map[x][y]==null){
            map[x][y] = new LocationInfo();
        }
        if(map[x][y].terrain!=null){
            return map[x][y].terrain;
        }
        MapLocation loc = new MapLocation(x,y);
        map[x][y].terrain = rc.senseTerrainTile(loc);
        if(map[x][y].terrain!=null){
            //TODO requiest the tile from comm
            //comm.requestTile(x,y);
        }
        return map[x][y].terrain;
    }
    public TerrainTile getTerrain(MapLocation loc){
        //doing this wastes some bytecodes
        return getTerrain(loc.x,loc.y);
    }
}
