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
    private MapLocation dest;
    private MapLocation currLocation;
    private MovementController motor;
    private static final int LONG_DISTANCE_THRESHOLD=25;
    private static final int MAX_ACTION_QUEUE_LENGTH=15;
    private Map map;

    public Navigator(RobotController rc, MovementController motor, Map map){
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
        actionQueue = new Direction[MAX_ACTION_QUEUE_LENGTH];
        actionQueueOffset = 0;
        if(currLocation.distanceSquaredTo(dest)>LONG_DISTANCE_THRESHOLD){
            //TODO implement
            //for now, it does the same thing as short distance planning
            MapLocation curr = currLocation;
            for(int i=0;i<MAX_ACTION_QUEUE_LENGTH;i++){
                Direction dir = curr.directionTo(dest);
                for(int j=0;j<8;j++){
                    if(isPassable(curr.add(dir))){
                        actionQueue[i]=dir;
                        break;
                    }
                    dir = dir.rotateLeft();
                }
            }

        }
        else{
            //short distance planning
            MapLocation curr = currLocation;
            for(int i=0;i<MAX_ACTION_QUEUE_LENGTH;i++){
                Direction dir = curr.directionTo(dest);
                for(int j=0;j<8;j++){
                    if(isPassable(curr.add(dir))){
                        actionQueue[i]=dir;
                        break;
                    }
                    dir = dir.rotateLeft();
                }
            }
        }
    }

    public void setDestination(MapLocation loc){
        dest = loc;
        doPathing();
    }

    public void doMovement(){
        if(motor.isActive() || actionQueue==null){
            return;
        }
         if(actionQueueOffset>=actionQueue.length){
            if(!currLocation.equals(dest)){
                doPathing();
            }
            return;
        }
        try{
            if(actionQueue[actionQueueOffset]==currDirection){
                //move forward
                if(motor.canMove(currDirection)){
                    motor.moveForward();
                    actionQueueOffset++;
                }
                else{
                    //TODO check if were stuck
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
}