package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

    private BasePlayer player;

    public RobotPlayer(RobotController rc) {
        switch(rc.getRobotType()){
            case ARCHON:
                player = new ArchonPlayer(rc);
                break;
            case SOLDIER:
                System.out.println("No Soldiers yet");
                break;
            case TURRET:
                System.out.println("No Turrets yet");
                break;
            case WOUT:
                player = new WoutPlayer(rc);
                break;
        }
    }

    public void run() {
        player.run();
    }
}
