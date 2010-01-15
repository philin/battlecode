<<<<<<< HEAD:ArchonPlayer.java
package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class ArchonPlayer implements BasePlayer {

    private final RobotController myRC;

    public ArchonPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        //System.out.println("STARTING");
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (myRC.isMovementActive()) {
                    myRC.yield();
                }
                Robot[] nbrs = myRC.senseNearbyGroundRobots();
                for(Robot r : nbrs)
                {
                    
                    RobotInfo info = myRC.senseRobotInfo(r);
                    if(info.location.distanceSquaredTo(myRC.getLocation()) <= 2 && 
                       info.type == RobotType.WOUT && info.team.equals(myRC.getTeam()))
                    {
                        double maxTransfer = info.maxEnergon - info.eventualEnergon;
                        if(maxTransfer < myRC.getEnergonLevel())
                        {
                            myRC.transferUnitEnergon(1, info.location, RobotLevel.ON_GROUND); 
                        }
                    }
                }
                MapLocation spawnLoc  = myRC.getLocation().add(myRC.getDirection());
                if(myRC.getEnergonLevel() > RobotType.WOUT.spawnCost() && 
                   myRC.senseTerrainTile(spawnLoc).getType() == TerrainTile.TerrainType.LAND 
                   && myRC.senseGroundRobotAtLocation(spawnLoc) == null) {
                    myRC.spawn(RobotType.WOUT);
                }
                else if (myRC.canMove(myRC.getDirection())) {
                    System.out.println("about to move");
                    myRC.moveForward();
                } else {
                    myRC.setDirection(myRC.getDirection().rotateRight());
                }
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
=======
package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class ArchonPlayer implements BasePlayer 
{
    private final RobotController myRC;

    public ArchonPlayer(RobotController rc) 
    {
        myRC = rc;
    }

    public void run() 
    {
        while (true) 
        {
            try 
            {
                /*** beginning of main loop ***/
                while (myRC.isMovementActive()) 
                {
                    myRC.yield();
                }

                if(myRC.getEnergonLevel() > RobotType.WOUT.spawnCost()) 
                {
                    myRC.spawn(RobotType.WOUT);
                }

                if (myRC.canMove(myRC.getDirection())) 
                {
                    System.out.println("about to move");
                    myRC.moveForward();
                }
                else 
                {
                    myRC.setDirection(myRC.getDirection().rotateRight());
                }
                myRC.yield();

                /*** end of main loop ***/
            } 
            catch (Exception e) 
            {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
>>>>>>> 653ff781349165a19ae5767e34642123d4203caf:ArchonPlayer.java
