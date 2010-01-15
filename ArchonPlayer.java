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

                if(myRC.getEnergonLevel() > RobotType.WOUT.spawnCost()) {
                    myRC.spawn(RobotType.WOUT);
                }

                if (myRC.canMove(myRC.getDirection())) {
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
