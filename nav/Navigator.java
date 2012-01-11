package team048.nav;

import java.util.LinkedList;

import battlecode.common.*;
import team048.mapping.Map;
import team048.*;

//XXX currently does navigation for ground units.
//I plan to make this abstract and create subclasses for ground and air units
//Buildings don't need Navigators :)
public class Navigator implements Module{
    //private BlockedMap blockedMap;
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
    //private MovementController motor;
    private int waitCount=0;
    private static final int STUCK_THRESHOLD=10;
    private static final int LONG_DISTANCE_THRESHOLD=25;
    private static final int MAX_ACTION_QUEUE_LENGTH=15;
    private PathPlanner pather;
    private Planner planner;
    private Map map;
    private RobotController rc;

    public Navigator(RobotController rc){
        this.rc = rc;
    }

    //XXX this will become abstract soon
    //Note, this will return the passibility independent of possible mobile
    //robots. i.e., if there is a robot on a tile, but it is otherwise passable,
    //it should return true, with the exception of buildings.
    //currently it just checks the terrain
    public boolean isPassable(MapLocation loc){
        return map.getTerrain(loc)==TerrainTile.LAND;
    }

    private void doPathing(){
        actionQueueOffset=0;
        if(enterDest || desiredDirection==Direction.OMNI){
            actionQueue = pather.planPath(dest);
        }
        else{
            actionQueue = pather.planPath(dest.add(desiredDirection.opposite()));
        }
        if(actionQueue==null){
            actionQueueLength=0;
        }
        else if(!enterDest && desiredDirection==Direction.OMNI){
            if(dest.equals(currLocation)){
                actionQueue = new Direction[2];
                for(int i=0;i<8;i++){
                    if(isPassable(currLocation.add(Util.intAsDirection(i)))){
                        actionQueue[0]=Util.intAsDirection(i);
                        actionQueue[1]=actionQueue[0].opposite();
                    }
                }
            }

            actionQueueLength = actionQueue.length-1;
        }
        else{
            actionQueueLength = actionQueue.length;
        }
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

    private void clearDest(){
        dest = null;
        desiredDirection = Direction.OMNI;
        actionQueue=null;
        actionQueueLength=0;
    }

    public boolean isActive(){
        return rc.isMovementActive();
    }

    public void setDirection(Direction dir){
        clearDest();
        try{
            turn(dir);
        }
        catch(GameActionException e){
        }
    }

    public void moveForward(){
        clearDest();
        try{
            driveForward();
        }
        catch(GameActionException e){
        }
    }

    private void turn(Direction dir) throws GameActionException{
        rc.setDirection(dir);
        currDirection = dir;
    }

    private void driveForward() throws GameActionException{
        rc.moveForward();
        map.didMove(currDirection);
        pather.didMove(currDirection);
        currLocation = currLocation.add(currDirection);
    }

    public void doMovement(){
        try{
            if(isActive() || actionQueue==null){
                return;
            }
            if(actionQueueOffset>=actionQueueLength){
                if(enterDest){
                    if(!currLocation.equals(dest)){
                        doPathing();
                    }
                    else if(desiredDirection!=Direction.OMNI){
                        turn(desiredDirection);
                        //destination reached
                        actionQueue=null;
                    }
                    else{
                        //destination reached
                        actionQueue=null;
                    }
                }
                else{
                    if(!currLocation.isAdjacentTo(dest)){
                        doPathing();
                    }
                    else if(desiredDirection==Direction.OMNI){
                        Direction dir = currLocation.directionTo(dest);
                        turn(dir);
                        actionQueue=null;
                    }
                    else{
                        turn(desiredDirection);
                        actionQueue=null;
                    }
                }
                return;
            }
            if(actionQueue[actionQueueOffset]==currDirection){
                //move forward
                if(rc.canMove(currDirection)){
                    if(rc.getDirection() != currDirection)
                    {
                        System.out.println("not matiching!!!!");
                    }
                    driveForward();
                    actionQueueOffset++;
                }
                else{
                    TerrainTile terrain = map.getTerrain(currLocation.add(currDirection));
                    if(terrain==TerrainTile.VOID ||
                       terrain==TerrainTile.OFF_MAP){
                        //replan
                        doPathing();
                        return;
                    }
                    waitCount++;
                    if(waitCount>STUCK_THRESHOLD){
                        //TODO unsticking logic
                        waitCount=0;
                    }
                }
            }
            else{
                turn(actionQueue[actionQueueOffset]);
            }
        }
        catch(GameActionException e){
            System.out.println("Navigator threw an exception, Round probably changed");
            e.printStackTrace();
        }
    }

    public boolean isAtDest(){
        return actionQueue==null && !isActive();
    }

    public void init(Planner planner){
        this.planner = planner;
        map = (Map)planner.getModule(ModuleType.MAPPING);
        if(map==null){
            System.out.println("Warning, there is no mapping module!");
        }
        pather = new AStar(map,rc);
        currLocation = rc.getLocation();
        currDirection = rc.getDirection();
        //MapLocation offset = map.getOffset();
        //if(offset!=null){
            //initialize the wavefront map
            //wavefrontInit();
        //}
    }
    public ModuleType getType(){
        return ModuleType.NAVIGATION;
    }

    public void doIdleTasks(){
        pather.doPlanning(0);
    }
}