package team046.nav;

import battlecode.common.*;
import team046.mapping.Map;

//XXX currently does navigation for ground units.
//I plan to make this abstract and create subclasses for ground and air units
//Buildings don't need Navigators :)
public class Navigator{
    private Direction[] actionQueue;
    private Direction currDirection;
    private int actionQueueOffset;
    private int actionQueueLength;
    private MapLocation dest;
    private Direction desiredDirection;
    private boolean enterDest=true;
    private MapLocation currLocation;
    private MovementController motor;
    private int waitCount=0;
    private static final int STUCK_THRESHOLD=10;
    private static final int LONG_DISTANCE_THRESHOLD=25;
    private static final int MAX_ACTION_QUEUE_LENGTH=15;
    private Map map;
    private RobotController rc;

    public Navigator(RobotController rc, MovementController motor, Map map){
        this.rc = rc;
        this.map = map;
        this.motor = motor;
        currLocation = rc.getLocation();
        currDirection = rc.getDirection();
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
        isPassable(currLocation);
        actionQueue = new Direction[MAX_ACTION_QUEUE_LENGTH];
        actionQueueOffset = 0;
        //short distance planning
        MapLocation curr = currLocation;
        int i;
        for(i=0;i<MAX_ACTION_QUEUE_LENGTH;i++){
            if(curr.equals(dest)){
                break;
            }
            Direction dir = curr.directionTo(dest);
            int j;
            if(map.getTerrain(curr.add(dir))==null){
                //unknown stuff, stop here
                if(i==0){
                    actionQueue[i]=curr.directionTo(dest);
                    i++;
                }
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
                if(i==0){
                    actionQueue[i]=curr.directionTo(dest);
                    i++;
                }
                break;
            }
        }
        actionQueueLength=i;
        if(!enterDest){
            actionQueueLength--;
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
                    if(desiredDirection!=Direction.OMNI){
                        motor.setDirection(desiredDirection);
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
                        actionQueue=null;
                    }
                    else{
                        motor.setDirection(desiredDirection);
                        actionQueue=null;
                    }
                }
                return;
            }
            if(actionQueue[actionQueueOffset]==currDirection){
                //move forward
                if(motor.canMove(currDirection)){
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
        }
    }

    public boolean isAtDest(){
        return actionQueue==null;
    }
}