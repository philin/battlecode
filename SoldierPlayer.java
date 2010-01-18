package team338;

import battlecode.common.*;
import java.util.Random;
public class SoldierPlayer extends BasePlayer
{
   
    public SoldierPlayer(RobotController rc)
    {
        super(rc);
    }
    
    public Behavior selectBehavior(Behavior b)
    {
        Behavior behavior = new Behavior(Behavior.BehaviorType.MOBILE_ATTACK_UNIT,
                                         null);
        return behavior;
        
    }
        
    public void tryMoveForward() throws GameActionException
    {
        if(myRC.canMove(myRC.getDirection()))
        {
            System.out.println("about to move");
            myRC.moveForward();
        }
        else
        {
            int dir = r.nextInt()%3;
            switch (dir)
            {
                case (0):
                {
                    myRC.setDirection(myRC.getDirection().rotateRight());
                    break;
                }
                case (1):
                {
                    myRC.setDirection(myRC.getDirection().rotateLeft());
                    break; 
                }
                case (2):
                {
                    myRC.setDirection(myRC.getDirection().opposite());
                    break;
                }
            } 
        }
    }

    protected void mobileAttackUnit(Object[] state) throws GameActionException
    {
        MapLocation loc = myRC.getLocation();
        Direction currDir = myRC.getDirection();
        MapLocation minArch = null;
        int minDistSquared=-1;
        for(MapLocation arch: myRC.senseAlliedArchons())
        {
            if(minArch==null)
            {
                minArch=arch;
                minDistSquared = arch.distanceSquaredTo(loc);
            }
            else
            {
                int dist = arch.distanceSquaredTo(loc);
                if(dist<minDistSquared){
                    minDistSquared=dist;
                    minArch=arch;
                }
            }
            if(minDistSquared<=1){
                break;
            }
        }
        if(minDistSquared<=2 && minArch != null){
            myRC.transferFlux(myRC.getFlux(),minArch,RobotLevel.IN_AIR);
        }

        //if can move
        if (!myRC.isMovementActive()){
            if(myRC.getEnergonLevel()<10)
            {
                while(minArch==null){
                    //we've lost
                    myRC.yield();
                }

                Direction dir = loc.directionTo(minArch);
                if(currDir==dir)
                {
                    tryMoveForward();
                }
                else if(dir==Direction.OMNI)
                {
                    //do noting
                }
                else
                {
                    if(myRC.canMove(dir))
                    {
                        myRC.setDirection(dir);
                    }
                    else{
                        tryMoveForward();
                    }
                }
            }
            else
            {
                tryMoveForward();
            }
        }
        else if (!myRC.isAttackActive())
        {
            //do an attack if possible
            Direction dir = currDir;
            Robot currTarget = null;
            RobotInfo currRi = null;
            do
            {
                MapLocation l = loc.add(dir);
                Robot g = myRC.senseGroundRobotAtLocation(loc.add(dir));
                RobotInfo ri = myRC.senseRobotInfo(g);
                Robot a = myRC.senseAirRobotAtLocation(loc.add(dir));
                RobotInfo airRi =  myRC.senseRobotInfo(a);
                if (ri.team!=team)
                {
                    //enemy
                    if(g != null)
                    {
                        if(currTarget == null || ri.type.isBuilding())
                        {
                            currTarget = g;
                            currRi = ri;
                        }
                    }
                }
                
                if(airRi.team != team)
                {
                
                    if (a!=null)
                    {
                        //enemy
                        if(currTarget == null)
                        {
                            currTarget = a;
                            currRi = airRi;
                        }
                    }
                }
                dir = dir.rotateRight();
            }while (dir!=currDir);
            
            if(currTarget != null)
                if(currRi.type.isAirborne())
                {
                    myRC.attackAir(currRi.location);
                }
                else
                {
                    myRC.attackGround(currRi.location);
                    
                }
            
        }
    }
           
}


    

