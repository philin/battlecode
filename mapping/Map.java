package team046.mapping;

import battlecode.common.*;

public class Map{
    public class LocationInfo{
        public TerrainTile terrain;
        public Mine mine;
        public Robot[] robots;
        public int updateRound;
        public LocationInfo(MapLocation loc){
            terrain = Map.this.rc.senseTerrainTile(loc);
        }
        public void addMine(Mine mine){
            this.mine = mine;
            updateRound = Clock.getRoundNum();
        }
    }
    private LocationInfo[][] map;
    private int size;
    private RobotController rc;
    private SensorController sensor;
    private int lastSenseRound=0;
    private static final int MIN_SENSE_WAIT=10;
    private static final int MAX_SIZE = 70;

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

    public void setSensor(SensorController controller){
        sensor = controller;
    }

    public void setTerrain(int x, int y, TerrainTile tile){
        map[x][y].terrain = tile;
    }

    public TerrainTile getTerrain(int x, int y){
        MapLocation loc = new MapLocation(x,y);
        return getTerrain(loc);
    }

    public TerrainTile getTerrain(MapLocation loc){
        TerrainTile tile = rc.senseTerrainTile(loc);
        //if(tile!=null){
        return tile;
        //}
        /*int x = loc.x;
        int y = loc.y;
        if(x<0 || x>=size || y<0 || y>=size){
            return TerrainTile.OFF_MAP;
        }
        if(map[x][y]==null){
            map[x][y] = new LocationInfo(loc);
            if(sensor.canSenseSquare(loc) &&
               lastSenseRound-Clock.getRoundNum()>MIN_SENSE_WAIT){
                lastSenseRound=Clock.getRoundNum();
                Mine[] mines = sensor.senseNearbyGameObjects(Mine.class);
                //XXX what we should really do is iterate over all locations in
                //the sensor range and set them. Right now
                //we use that last round we sensed as a heuristic
                for(Mine m : mines){
                    MapLocation mineLoc = m.getLocation();
                    if(map[mineLoc.x][mineLoc.y]==null){
                        map[mineLoc.x][mineLoc.y] = new LocationInfo(mineLoc);
                        map[mineLoc.x][mineLoc.y].addMine(m);
                    }
                }
            }
        }
        else{
            if(map[x][y].terrain!=null){
                return map[x][y].terrain;
            }
            map[x][y].terrain = rc.senseTerrainTile(loc);
            if(map[x][y].terrain!=null){
                //TODO requiest the tile from comm
                //comm.requestTile(x,y);
            }
        }
        return map[x][y].terrain;
        */
    }
}
