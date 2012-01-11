package team048.mapping;

import battlecode.common.*;

import team048.*;
import team048.nav.*;

//the map itself is not actually aware of where any of the locations are in absolute coordinates, only relative to eachother
public class Map implements Module{
    public class Node{
        public TerrainTile terrain;
        public NavInfo navInfo;//path planning data goes here
        public Node[] neighbors;
        public Node(){
            terrain = null;
            navInfo = new NavInfo();
            neighbors = new Node[8];
        }
        public void updateTerrain(TerrainTile tile){
            if(terrain==tile){
                return;
            }
            terrain=tile;
            if(tile==null || tile==TerrainTile.LAND){
                return;
            }
            else{
                for(int i=0;i<8;i++){
                    if(neighbors[i]!=null){
                        neighbors[i].neighbors[(i+4)%8]=null;
                        neighbors[i]=null;
                    }
                }
            }
        }
    }
    public static final int MAP_SIZE=72;//2 tiles of padding
    Node map[][];
    Node currNode;
    RobotController myRC;

    public Map(RobotController myRC){
        this.myRC=myRC;
        map = new Node[MAP_SIZE][];
        for(int i=0;i<MAP_SIZE;i++){
            map[i] = new Node[MAP_SIZE];
            for(int j=0;j<MAP_SIZE;j++){
                map[i][j] = new Node();
            }
        }
        for(int i=0;i<MAP_SIZE;i++){
            for(int j=0;j<MAP_SIZE;j++){
                //setup neighbors
                //it might be worth while to put this code somewhere else
                //(0,0) is in the northwest
                //remember 0=north, go clockwise
                map[i][j].neighbors[0]=map[i][(j+MAP_SIZE-1)%MAP_SIZE];
                map[i][j].neighbors[1]=map[(i+1)%MAP_SIZE][(j+MAP_SIZE-1)%MAP_SIZE];
                map[i][j].neighbors[2]=map[(i+1)%MAP_SIZE][j];
                map[i][j].neighbors[3]=map[(i+1)%MAP_SIZE][(j+1)%MAP_SIZE];
                map[i][j].neighbors[4]=map[i][(j+1)%MAP_SIZE];
                map[i][j].neighbors[5]=map[(i+MAP_SIZE-1)%MAP_SIZE][(j+1)%MAP_SIZE];
                map[i][j].neighbors[6]=map[(i+MAP_SIZE-1)%MAP_SIZE][j];
                map[i][j].neighbors[7]=map[(i+MAP_SIZE-1)%MAP_SIZE][(j+MAP_SIZE-1)%MAP_SIZE];
            }
        }
    }

    //call this every time the robot moves
    public void didMove(Direction dir){
        currNode = currNode.neighbors[Util.directionAsInt(dir)];
    }

    private TerrainTile quickUpdateTerrain(MapLocation loc){
        TerrainTile ret = map[loc.x%MAP_SIZE][loc.y%MAP_SIZE].terrain;
        if(ret==null){
            ret = myRC.senseTerrainTile(loc);
            map[loc.x%MAP_SIZE][loc.y%MAP_SIZE].updateTerrain(ret);
        }
        return ret;
    }

    public void removeVertical(int x){
        //stupid mode for now, this can be optimized
        for(int y=0;y<MAP_SIZE;y++){
            map[x][y].updateTerrain(TerrainTile.OFF_MAP);
        }
    }

    public void removeHorizontal(int y){
        //same comment as removeVertical
        for(int x=0;x<MAP_SIZE;x++){
            map[x][y].updateTerrain(TerrainTile.OFF_MAP);
        }
    }

    public TerrainTile getTerrain(MapLocation loc){
        TerrainTile ret = map[loc.x%MAP_SIZE][loc.y%MAP_SIZE].terrain;
        if(ret==null){
            ret = myRC.senseTerrainTile(loc);
            map[loc.x%MAP_SIZE][loc.y%MAP_SIZE].updateTerrain(ret);
            if(ret==TerrainTile.OFF_MAP){
                MapLocation currLocation = myRC.getLocation();
                if(currLocation.x==loc.x){
                    //horizontal
                    removeHorizontal(loc.y%MAP_SIZE);
                }
                else if(currLocation.y==loc.y){
                    //vertical
                    removeVertical(loc.x%MAP_SIZE);
                }
                else{
                    //who knows
                    if(currLocation.x>loc.x){
                        TerrainTile e = quickUpdateTerrain(loc.add(Direction.EAST));
                        if(e!=null && e!=TerrainTile.OFF_MAP){
                            removeVertical(loc.x%MAP_SIZE);
                        }
                    }
                    else{
                        TerrainTile w = quickUpdateTerrain(loc.add(Direction.WEST));
                        if(w!=null && w!=TerrainTile.OFF_MAP){
                            removeVertical(loc.x%MAP_SIZE);
                        }
                    }
                    if(currLocation.y>loc.y){
                        TerrainTile s = quickUpdateTerrain(loc.add(Direction.SOUTH));
                        if(s!=null && s!=TerrainTile.OFF_MAP){
                            removeHorizontal(loc.y%MAP_SIZE);
                        }
                    }
                    else{
                        TerrainTile n = quickUpdateTerrain(loc.add(Direction.NORTH));
                         if(n!=null && n!=TerrainTile.OFF_MAP){
                             removeHorizontal(loc.y%MAP_SIZE);
                        }
                    }

                }
            }
        }
        return ret;
    }

    public NavInfo getNavInfo(MapLocation loc){
        return map[loc.x%MAP_SIZE][loc.y%MAP_SIZE].navInfo;
    }

    public Node getNode(MapLocation loc){
        return map[loc.x%MAP_SIZE][loc.y%MAP_SIZE];
    }

    public Node getCurrNode(){
        return currNode;
    }

    public ModuleType getType(){
        return ModuleType.MAPPING;
    }

    public void init(Planner p){
        //STUB
        currNode = getNode(myRC.getLocation());
    }

    public void doIdleTasks(){
    }
}

