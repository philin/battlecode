package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.mapping.*;
import team046.nav.*;
import team046.*;

public class BasicBuilder extends Unit
{
    private int minecheckcount = 0;
    private static final int MINECHECKDELAY = 5;

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
    private MapLocation targetMine = null;

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
                    Mine[] mines = this.sensor.senseNearbyGameObjects(Mine.class);
                    for(Mine m : mines){
                        MapLocation mineLoc = m.getLocation();
                        if (this.sensor.senseObjectAtLocation(mineLoc, RobotLevel.ON_GROUND) == null)
                        {
                            //location is available
                            this.currentstate = SPOTTED_MINE;
                            System.out.println("spotted mine");
                            targetMine = mineLoc;
                            return;
                        }
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
        navigator.setDestination(targetMine, false);
        while (true)
        {
            myRC.yield();

            if (!this.navigator.isAtDest())
            {
                navigator.doMovement();
            }
            else
            {

                System.out.println("reached mine");
                this.currentstate = BUILDING_ON_MINE;
                return;
            }
        }

    }

    private void buildingOnMineBehavior()
    {
        while (true)
        {
            myRC.yield();
            System.out.println("building mine");
            try{
                if (myRC.getTeamResources() > Chassis.BUILDING.cost &&
                    this.sensor.senseObjectAtLocation(this.targetMine, RobotLevel.ON_GROUND) == null)
                {

                    Util.buildUnit(this.myRC,
                                   this.builder,
                                   UnitCommon.BASIC_BUILDING,
                                   this.targetMine);

                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            this.targetMine = null;
            this.currentstate = FORAGING;
            return;
        }

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
            else if(components[i].type() == ComponentType.SIGHT)
            {
                System.out.println("sensor found");
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
                spottedMineBehavior();
                break;
            case BUILDING_ON_MINE:
                buildingOnMineBehavior();
                break;
            default:
                this.myRC.yield();
                break;
            }
        }

    }
}
