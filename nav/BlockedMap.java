package team046.nav;

import battlecode.common.*;

import team046.*;
import team046.mapping.*;

public class BlockedMap{
    private static final int BLOCK_SIZE = 5;
    private class MapBlock implements IdleTask{
        //are their hostiles in the block (last check)
        boolean hasEnemies;
        MapLocation corner;
        boolean isMapComplete = false;
        int stuckCount=0;
        double cost = 150;
        TerrainTile regionMap[][];
        int globalX;
        int globalY;
        int missingCount;
        public MapBlock(MapLocation corner){
            this.corner = corner;
        }

        public double getCost(){
            return cost;
        }

        public void gotStuck(){
            stuckCount++;
            cost*=1.1;
        }

        //analyze the block
        private static final int STATE_READ=0;
        private static final int STATE_ANALYZE=1;
        int state=STATE_READ;

        public boolean executeTask(){
            if(state==STATE_READ){
                for(int x=0;x<BLOCK_SIZE;x++){
                    for(int y=0;y<BLOCK_SIZE;y++){
                        if(regionMap[x][y]==null){
                            if((regionMap[x][y]=map.getTerrainQuick(corner.add(x,y)))!=null){
                                missingCount--;
                            }
                        }
                    }
                }
                state=STATE_ANALYZE;
                return true;
            }
            else{
                int freeBlockCount=0;
                int voidBlockCount=0;
                int unknownBlockCount=0;
                for(int x=0;x<BLOCK_SIZE;x++){
                    for(int y=0;y<BLOCK_SIZE;y++){
                        TerrainTile terrain = regionMap[x][y];
                        if(terrain==null){
                            isMapComplete=false;
                            unknownBlockCount++;
                        }
                        else if(terrain==TerrainTile.VOID){
                            voidBlockCount++;
                        }
                        else if(terrain==TerrainTile.LAND){
                            freeBlockCount++;
                        }

                    }
                }
                if(freeBlockCount==0 && unknownBlockCount==0){
                    cost=-1;
                }
                cost = Math.pow(1.1,stuckCount)*Math.pow(1.5,(voidBlockCount+.5*unknownBlockCount)/(voidBlockCount+freeBlockCount+unknownBlockCount));
                if(missingCount>0){
                    state=STATE_READ;
                    return true;
                }
                //block completely done
                return false;
            }
        }
    }
    MapBlock[][] blockedMap;
    MapLocation offset;
    int size;
    Map map;
    Planner planner;
    private static final int MAX_MAP_SIZE = 70/BLOCK_SIZE;

    public BlockedMap(Map map, Planner planner){
        this.planner = planner;
        this.map = map;
        offset = map.getOffset();
        size = ((map.getRealSize()+4)/5);//make it round up
        blockedMap = new MapBlock[size][];
        for(int i=0;i<size;i++){
            blockedMap[i] = new MapBlock[size];
            for(int j=0;j<size;j++){
                blockedMap[i][j] = new MapBlock(offset.add(i*5,j*5));
                planner.addIdleTask(blockedMap[i][j]);
            }
        }
    }

    int[] toBlockCoordinates(MapLocation loc){
        return new int[]{(loc.x-offset.x)/5,(loc.y-offset.y)/5};
    }

    //in block coordinates
    public double getCost(int x,int y){
        if(x<0 || x>=size || y<0 || y>=size){
            return -1;
        }
        return blockedMap[x][y].getCost();
    }

    public void gotStuck(MapLocation loc){
        int[] coord = toBlockCoordinates(loc);
        blockedMap[coord[0]][coord[1]].gotStuck();
    }
}