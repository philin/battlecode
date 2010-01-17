package team338;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable
{
    private BasePlayer player;

    public RobotPlayer(RobotController rc)
    {
        switch(rc.getRobotType()){
        case ARCHON:
            player = new ArchonPlayer(rc);
            break;
        case AURA:
            player = new AuraPlayer(rc);
            break;
        case CHAINER:
            player = new ChainerPlayer(rc);
            break;
        case COMM:
            player = new CommPlayer(rc);
            break;
        case SOLDIER:
            player = new SoldierPlayer(rc);
            break;
        case TELEPORTER:
            player = new TeleporterPlayer(rc);
            break;
        case TURRET:
            player = new TurretPlayer(rc);
            break;
        case WOUT:
            player = new WoutPlayer(rc);
            break;
        }
    }

    public void run()
    {
        player.run();
    }
}
