package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.mapping.*;
import team046.nav.*;

public class BasicBuilder extends Unit
{
    private int minecheckcount = 0;
    private static final int MINECHECKDELAY = 3;

    //controllers
    RobotController myRC = null;
    BuilderController builder = null;
    MovementController motor = null;
    SensorController sensor = null;

    Map map = null;
    Navigator navigator = null;
    Random rand = null;

    //states
    private static final int FORAGING = 0;
    private static final int SPOTTED_MINE = 1;
    private static final int BUILDING_ON_MINE = 2;

    private int currentstate = 0; //default to foraging behavior

    public BasicBuilder()
    {
    }

    public int getType()
    {
        return UnitCommon.BASIC_BUILDER;
    }

    private void forageBehavior()
    {
        int changeCounter = 0;
        try
        {
            while (true)
            {
                myRC.yield();

                navigator.doMovement();
                changeCounter++;
                if(changeCounter>10){
                    //choose another location
                    MapLocation newLocation
                        = myRC.getLocation().add(5*(rand.nextInt(10)-4),
                                                 5*(rand.nextInt(10)-4));
                    changeCounter = 0;
                    navigator.setDestination(newLocation);
                }
                if ( this.minecheckcount == MINECHECKDELAY )
                {
                    Mine[] mines = sensor.senseNearbyGameObjects(Mine.class);
                    for(Mine m : mines){
                        MapLocation mineLoc = m.getLocation();
                    }
                    this.minecheckcount = 0;
                }
                else
                {
                    this.minecheckcount++;
                }


            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void spottedMineBehavior()
    {

    }

    private void buildingOnMineBehavior()
    {

    }
    public void runBehavior(RobotController myRC)
    {
        this.myRC = myRC;
        ComponentController [] components = this.myRC.components();

        for(int i = 0; i < components.length; ++i)
        {
            if(components[i].type() == ComponentType.CONSTRUCTOR)
            {
                this.builder = (BuilderController)components[i];
            }
            else if(components[i] instanceof MovementController)
            {
                this.motor = (MovementController)components[i];
            }
            else if(components[i] instanceof SensorController)
            {
                this.sensor = (SensorController)components[i];
            }
        }

        this.map = new Map(this.myRC);
        this.navigator = new Navigator(this.myRC, this.motor, this.map);

        this.rand = new Random();

        while (true)
        {
            switch (this.currentstate)
            {
            case FORAGING:
                forageBehavior();
                break;
            case SPOTTED_MINE:

                break;
            case BUILDING_ON_MINE:
                break;
            default:
                this.myRC.yield();
                break;
            }
        }

    }
}
