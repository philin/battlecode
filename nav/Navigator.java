package team046.nav;

import java.util.LinkedList;

import battlecode.common.*;
import team046.mapping.Map;
import team046.*;

//XXX currently does navigation for ground units.
//I plan to make this abstract and create subclasses for ground and air units
//Buildings don't need Navigators :)
public class Navigator implements Module{
    private BlockedMap blockedMap;
    private boolean[][] blacklistedBlocks;
    private Direction[] actionQueue;
    private Direction currDirection;
    private int actionQueueOffset;
    private int actionQueueLength;
    private MapLocation prevDest;
    private MapLocation dest;
    private Direction desiredDirection;
    private boolean enterDest=true;
    private MapLocation currLocation;
    private MovementController motor;
    private int waitCount=0;
    private static final int STUCK_THRESHOLD=10;
    private static final int LONG_DISTANCE_THRESHOLD=25;
    private static final int MAX_ACTION_QUEUE_LENGTH=15;
    private Planner planner;
    private Map map;
    private RobotController rc;

    public Navigator(RobotController rc, MovementController motor){
        this.rc = rc;
        this.motor = motor;
    }

    //XXX this will become abstract soon
    //Note, this will return the passibility independent of possible mobile
    //robots. i.e., if there is a robot on a tile, but it is otherwise passable,
    //it should return true, with the exception of buildings.
    //currently it just checks the terrain
    public boolean isPassable(MapLocation loc){
        return map.getTerrain(loc)==TerrainTile.LAND;
    }

    private void doShortDistancePathing(MapLocation dest, boolean enterDest){
        MapLocation curr = currLocation;
        int i;
        //rc.yield();
        for(i=0;i<MAX_ACTION_QUEUE_LENGTH;i++){
            if(curr.equals(dest)){
                break;
            }
            Direction dir = curr.directionTo(dest);
            int j;
            if(map.getTerrain(curr.add(dir))==null){
                //unknown stuff, stop here
                break;
            }
            for(j=0;j<8;j++){
                if(isPassable(curr.add(dir))){
                    actionQueue[i]=dir;
                    curr = curr.add(dir);
                    break;
                }
                dir = dir.rotateLeft();
            }
            //XXX cleanup
            if(j==8){
                break;
            }
        }
        actionQueueLength=i;
        if(!enterDest){
            actionQueueLength--;
        }
        //rc.yield();
    }
    //put here so we don't have to reallocate every time
    LinkedList<int[]> wavefrontQueue = new LinkedList<int[]>();
    private static final double SQRT_2 = Math.sqrt(2);
    double[][] wavefrontCostMap;
    //x,y should be in blockMap coordinates
    private void runWavefront(int destX, int destY, int startX, int startY){
        //this clears the array
        for(int i=0;i<wavefrontCostMap.length;i++){
            wavefrontCostMap[i]=new double[wavefrontCostMap.length];
        }
        wavefrontCostMap[destX][destY]=1;
        wavefrontQueue.clear();
        wavefrontQueue.add(new int[]{destX,destY});
        do{
            double currCost = wavefrontCostMap[destX][destY];
            double cost;
            //North
            cost = blockedMap.getCost(destX,destY-1);
            if(!blacklistedBlocks[destX][destY-1]){
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX][destY-1]==0 ||
                       wavefrontCostMap[destX][destY-1]>cost){
                        wavefrontCostMap[destX][destY-1]=cost;
                    }
                }
            }
            //NorthEast
            if(!blacklistedBlocks[destX+1][destY-1]){
                cost = blockedMap.getCost(destX+1,destY-1)*SQRT_2;
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX+1][destY-1]==0 ||
                       wavefrontCostMap[destX+1][destY-1]>cost){
                        wavefrontCostMap[destX+1][destY-1]=cost;
                    }
                }
            }
            //East
            if(!blacklistedBlocks[destX+1][destY]){
                cost = blockedMap.getCost(destX+1,destY);
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX+1][destY]==0 ||
                       wavefrontCostMap[destX+1][destY]>cost){
                        wavefrontCostMap[destX+1][destY]=cost;
                    }
                }
            }
            //SouthEast
            if(!blacklistedBlocks[destX+1][destY+1]){
                cost = blockedMap.getCost(destX+1,destY+1)*SQRT_2;
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX+1][destY+1]==0 ||
                       wavefrontCostMap[destX+1][destY+1]>cost){
                        wavefrontCostMap[destX+1][destY+1]=cost;
                    }
                }
            }
            //South
            if(!blacklistedBlocks[destX][destY+1]){
                cost = blockedMap.getCost(destX,destY+1);
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX][destY+1]==0 ||
                       wavefrontCostMap[destX][destY+1]>cost){
                        wavefrontCostMap[destX][destY+1]=cost;
                    }
                }
            }
            //SouthWest
            if(!blacklistedBlocks[destX-1][destY+1]){
                cost = blockedMap.getCost(destX-1,destY+1)*SQRT_2;
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX+1][destY+1]==0 ||
                       wavefrontCostMap[destX-1][destY+1]>cost){
                        wavefrontCostMap[destX-1][destY+1]=cost;
                    }
                }
            }
            //West
            if(!blacklistedBlocks[destX-1][destY]){
                cost = blockedMap.getCost(destX-1,destY);
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX-1][destY]==0 ||
                       wavefrontCostMap[destX-1][destY]>cost){
                        wavefrontCostMap[destX-1][destY]=cost;
                    }
                }
            }
            //NorthWest
            if(!blacklistedBlocks[destX-1][destY-1]){
                cost = blockedMap.getCost(destX-1,destY-1)*SQRT_2;
                if(cost>=0){
                    cost+=currCost;
                    if(wavefrontCostMap[destX-1][destY-1]==0 ||
                       wavefrontCostMap[destX-1][destY-1]>cost){
                        wavefrontCostMap[destX-1][destY-1]=cost;
                    }
                }
            }
        }while(!wavefrontQueue.isEmpty());
    }

    private void doPathing(){
        isPassable(currLocation);
        actionQueue = new Direction[MAX_ACTION_QUEUE_LENGTH];
        actionQueueOffset = 0;
        MapLocation tempDest = null;
        if(currLocation.distanceSquaredTo(dest)>LONG_DISTANCE_THRESHOLD){
            if(prevDest!=dest){
                //new destination, redo wavefront
                prevDest=dest;
            }
        }
        //short distance planning
        doShortDistancePathing(tempDest,enterDest);
    }

    public void setDestination(MapLocation loc, Direction direction,
                               boolean enterDest){
        desiredDirection = direction;
        dest = loc;
        this.enterDest = enterDest;
        doPathing();
    }

    public void setDestination(MapLocation loc, boolean enterDest){
        setDestination(loc,Direction.OMNI,enterDest);
    }

    public void setDestination(MapLocation loc, Direction direction){
        setDestination(loc,direction,true);
    }

    public void setDestination(MapLocation loc){
        setDestination(loc,Direction.OMNI,true);
    }

    public void doMovement(){
        try{
            if(motor.isActive() || actionQueue==null){
                return;
            }
            if(actionQueueOffset>=actionQueueLength){
                if(enterDest){
                    if(!currLocation.equals(dest)){
                        doPathing();
                    }
                    else if(desiredDirection!=Direction.OMNI){
                        motor.setDirection(desiredDirection);
                        currDirection = desiredDirection;
                        //destination reached
                        actionQueue=null;
                    }
                }
                else{
                    if(!currLocation.isAdjacentTo(dest)){
                        doPathing();
                    }
                    else if(desiredDirection==Direction.OMNI){
                        motor.setDirection(actionQueue[actionQueueLength]);
                        currDirection = actionQueue[actionQueueOffset];
                        actionQueue=null;
                    }
                    else{
                        motor.setDirection(desiredDirection);
                        currDirection = desiredDirection;
                        actionQueue=null;
                    }
                }
                return;
            }
            if(actionQueue[actionQueueOffset]==currDirection){
                //move forward
                if(motor.canMove(currDirection)){
                    if(rc.getDirection() != currDirection)
                    {
                        System.out.println("not matiching!!!!");
                    }
                    motor.moveForward();
                    actionQueueOffset++;
                    currLocation = currLocation.add(currDirection);
                }
                else{
                    waitCount++;
                    if(waitCount>STUCK_THRESHOLD){
                        //TODO unsticking logic
                        waitCount=0;
                    }
                }
            }
            else{
                motor.setDirection(actionQueue[actionQueueOffset]);
                currDirection = actionQueue[actionQueueOffset];
            }
        }
        catch(GameActionException e){
            System.out.println("Navigator threw an exception, Round probably changed");
            e.printStackTrace();
        }
    }

    public boolean isAtDest(){
        return actionQueue==null && !motor.isActive();
    }

    private void wavefrontInit(){
        new BlockedMap(map,planner);
    }

    public void init(Planner planner){
        this.planner = planner;
        map = (Map)planner.getModule(ModuleType.MAPPING);
        if(map==null){
            System.out.println("Warning, there is no mapping module!");
        }
        currLocation = rc.getLocation();
        currDirection = rc.getDirection();
        MapLocation offset = map.getOffset();
        if(offset!=null){
            //initialize the wavefront map
            wavefrontInit();
        }
    }
    public ModuleType getType(){
        return ModuleType.NAVIGATION;
    }
}