package team046.nav;

import java.util.LinkedList;

import battlecode.common.*;
import team046.mapping.Map;
import team046.*;

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
    private MovementController motor;
    private int waitCount=0;
    private static final int STUCK_THRESHOLD=10;
    private static final int LONG_DISTANCE_THRESHOLD=25;
    private static final int MAX_ACTION_QUEUE_LENGTH=15;
    private PathPlanner pather;
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

    private void doPathing(){
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
            //XXX if we are on the tile, and enterDest and desiredDirection==OMNI bad stuff might happen.
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

    public void moveForward() throws GameActionException{
        map.didMove(currDirection);
        pather.didMove(currDirection);
        motor.moveForward();
        currLocation = currLocation.add(currDirection);
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
                        currDirection = actionQueue[actionQueueLength];
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
                    moveForward();
                    actionQueueOffset++;
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
}