package team046.mapping;

import battlecode.common.*;

import team046.*;

public class Map implements Module{
    public class LocationInfo{
        public TerrainTile terrain;
        public Mine mine;
        public Robot[] robots;
        public int updateRound;
        public LocationInfo(TerrainTile tile){
            terrain = tile;
        }
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
    private int realSize=-1;
    private boolean isOffset = false;
    private int xWest=-1, xEast=-1;
    private int yNorth=-1, ySouth=-1;
    private MapLocation offset;
    private RobotController rc;
    private SensorController sensor;
    private int lastSenseRound=0;
    private static final int MIN_SENSE_WAIT=10;
    private static final int MAX_MAP_SIZE = 72;

    public Map(RobotController rc){
        this.rc = rc;
        this.size = MAX_MAP_SIZE;
        map = new LocationInfo[size][];
        for(int i=0;i<size;i++){
            map[i] = new LocationInfo[size];
        }
    }
    //use this version if we know the size of the map
    public Map(RobotController rc, MapLocation offset, int size){
        this.rc = rc;
        this.size = size;
        realSize = size;
        isOffset = true;
        this.offset = offset;
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
        int x,y;
        if(isOffset){
            x = loc.x-offset.x;
            y = loc.y-offset.y;
            if(x<0 || x>=size || y<0 || y>=size){
                return TerrainTile.OFF_MAP;
            }
        }
        else{
            x = loc.x%MAX_MAP_SIZE;
            y = loc.y%MAX_MAP_SIZE;
            if(offset!=null){
                int otherX = loc.x-offset.x;
                int otherY = loc.y-offset.y;
                if(otherX<0 || otherX>size || otherY<0 || otherY>size){
                    return TerrainTile.OFF_MAP;
                }
            }
        }
        //x and y are now in array coordinates
        if(map[x][y]!=null){
            return map[x][y].terrain;
        }
        TerrainTile tile = rc.senseTerrainTile(loc);
        if(tile!=null){
            map[x][y] = new LocationInfo(tile);
            if(tile==TerrainTile.OFF_MAP && (offset==null || realSize<0)){
                TerrainTile se = rc.senseTerrainTile(loc.add(1,1));
                TerrainTile sw = rc.senseTerrainTile(loc.add(-1,1));
                TerrainTile nw = rc.senseTerrainTile(loc.add(-1,-1));
                TerrainTile ne = rc.senseTerrainTile(loc.add(1,-1));
                if(se==TerrainTile.LAND || se==TerrainTile.VOID){
                    if(sw==TerrainTile.LAND || sw==TerrainTile.VOID){
                        //northern boundary
                        yNorth = loc.y+1;
                        if(xWest>=0){
                            offset = new MapLocation(yNorth,xWest);
                            if(xEast>=0 && ySouth>=0){
                                realSize = Math.max(ySouth-yNorth,xWest-xEast);
                            }
                        }
                    }
                    else if(ne==TerrainTile.LAND || ne==TerrainTile.VOID){
                        //western boundary
                        xWest = loc.x+1;
                        if(yNorth>=0){
                            offset = new MapLocation(yNorth,xWest);
                            if(xEast>=0 && ySouth>=0){
                                realSize = Math.max(ySouth-yNorth,xWest-xEast);
                            }
                        }
                    }
                    else{
                        //corner !
                        offset = loc.add(1,1);
                        xWest = offset.x;
                        yNorth = offset.y;
                        if(xEast>=0 && ySouth>=0){
                            realSize = Math.max(ySouth-yNorth,xWest-xEast);
                        }
                    }
                }
                else if(nw==TerrainTile.LAND || nw==TerrainTile.VOID){
                    if(ne==TerrainTile.LAND || ne==TerrainTile.VOID){
                        //southern boundary
                        ySouth = loc.y;
                        if(xEast>=0 && xWest>=0 && yNorth>=0){
                            realSize = Math.max(ySouth-yNorth,xWest-xEast);
                        }
                    }
                    else if(sw==TerrainTile.LAND || sw==TerrainTile.VOID){
                        //eastern boundary
                        xEast = loc.x;
                        if(xWest>=0 && yNorth>=0 && ySouth>=0){
                            realSize = Math.max(ySouth-yNorth,xWest-xEast);
                        }
                    }
                    else{
                        //corner
                        xEast = loc.x;
                        ySouth = loc.y;
                        if(xWest>=0 && yNorth>=0){
                            realSize = Math.max(ySouth-yNorth,xWest-xEast);
                        }
                    }
                }
                else if(ne==TerrainTile.LAND || ne==TerrainTile.VOID){
                    //south west corner
                    xWest = loc.x+1;
                    ySouth = loc.y;
                    if(xEast>=0){
                        offset = new MapLocation(yNorth,xWest);
                        if(yNorth>=0){
                            realSize = Math.max(ySouth-yNorth,xWest-xEast);
                        }
                    }
                }
                else if(sw==TerrainTile.LAND || sw==TerrainTile.VOID){
                    //north east corner
                    xEast = loc.x;
                    yNorth = loc.y+1;
                    if(xWest>=0){
                        offset = new MapLocation(yNorth,xWest);
                        if(ySouth>=0){
                            realSize = Math.max(ySouth-yNorth,xWest-xEast);
                        }
                    }
                }
            }
            else if(sensor != null && sensor.canSenseSquare(loc) &&
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
            return map[x][y].terrain;
        }
        return null;
    }

    public int getRealSize(){
        return realSize;
    }

    public MapLocation getOffset(){
        return offset;
    }

    public void init(Planner planner){
        //STUB
    }

    public ModuleType getType(){
        return ModuleType.MAPPING;
    }
}
