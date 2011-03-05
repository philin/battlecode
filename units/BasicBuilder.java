package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.mapping.*;
import team046.nav.*;
import team046.*;

public class BasicBuilder extends Unit
{
    private int scancount = 0;
    private static final int SCAN_DELAY = 6;

    //controllers
    BuilderController builder = null;
    MovementController motor = null;
    SensorController sensor = null;

    Map map = null;
    Navigator navigator = null;
    Random rand = null;


    //states
    private static final int BUILDING_FACTORY = -1;
    private static final int FORAGING = 0;
    private static final int SPOTTED_MINE = 1;
    private static final int BUILDING_ON_MINE = 2;
    private static final int SPOTTED_INACTIVE_ROBOT = 3;
    private static final int ACTIVATING_ROBOT = 4;


    private int currentstate = -1; //default to foraging behavior
    private MapLocation targetMine = null;
    private MapLocation targetUnit = null;

    public BasicBuilder(RobotController myRC)
    {
        super(myRC);
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
                this.sensor = (SensorController)components[i];
            }
        }
        this.map = new Map(this.myRC);
        addModule(map);
        this.navigator = new Navigator(this.myRC, this.motor);
        addModule(navigator);
        this.rand = new Random();
        init();//initializes all added modules
    }

    public int getType()
    {
        return UnitCommon.BASIC_BUILDER;
    }

    private boolean isFactory(RobotInfo ri)
    {
        ComponentType[] componentList = ri.components;
        for(int i = 0; i < componentList.length; ++i)
        {
            if(componentList[i] == ComponentType.FACTORY)
            {
                return true;
            }

        }

        return false;
    }

    private void buildingFactoryBehavior()
    {
        try{
            for(int i = 0; i < 4; ++i)
            {
                Robot[] nearbyFactories = this.sensor.senseNearbyGameObjects(Robot.class);
                for(int j = 0; j < nearbyFactories.length; ++j)
                {
                    RobotInfo ri = this.sensor.senseRobotInfo(nearbyFactories[j]);
                    if(isFactory(ri))
                    {
                        currentstate = 0;
                        return;
                    }
                }
                while(this.motor.isActive())
                    myRC.yield();

                navigator.setDirection(myRC.getDirection().rotateRight());
            }
            //no factories found near spawn point
            //build a factory
            for(int i = 0; i < 4; ++i)
            {
                if(myRC.getTeamResources() > 250 &&
                   this.builder.canBuild(Chassis.BUILDING,
                                         myRC.getLocation().add(myRC.getDirection())))
                {
                    Util.buildUnit(myRC,
                                   builder,
                                   UnitCommon.FACTORY,
                                   myRC.getLocation().add(myRC.getDirection()));
                    break;
                }
                while(this.motor.isActive())
                    myRC.yield();
                navigator.setDirection(myRC.getDirection().rotateRight());

            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        currentstate = 0;
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
                    int dx = 15*(rand.nextInt(11)-5);
                    int dy = 15*(rand.nextInt(11)-5);
                    MapLocation newLocation
                        = myRC.getLocation().add(dx, dy);

                    Direction currdirection = myRC.getDirection();
                    if(currdirection == myRC.getLocation().directionTo(newLocation).opposite())
                    {
                        dx = -dx;
                        dy = -dy;
                        newLocation = myRC.getLocation().add(dx, dy);
                    }
                    changeCounter = 0;
                    navigator.setDestination(newLocation);
                    myRC.yield();
                }

                if ( this.scancount == SCAN_DELAY )
                {

                    Mine[] mines = this.sensor.senseNearbyGameObjects(Mine.class);

                    for(Mine m : mines){
                        MapLocation mineLoc = m.getLocation();
                        if (this.sensor.senseObjectAtLocation(mineLoc, RobotLevel.ON_GROUND) == null)
                        {
                            //location is available
                            this.currentstate = SPOTTED_MINE;
                            targetMine = mineLoc;
                            return;
                        }
                    }

                    if (myRC.getTeamResources() > 100)
                    {
                        myRC.yield();
                        Robot[] nearbyRobots = this.sensor.senseNearbyGameObjects(Robot.class);

                        for(Robot r : nearbyRobots)
                        {
                            RobotInfo rinfo = this.sensor.senseRobotInfo(r);
                            //if allied and off
                            if(myRC.getTeam() == rinfo.robot.getTeam()
                               && !rinfo.on
                               && rinfo.robot.getRobotLevel() == RobotLevel.ON_GROUND)
                            {
                                //if robot is inactive
                                this.targetUnit = rinfo.location;
                                this.currentstate = SPOTTED_INACTIVE_ROBOT;

                                return;
                            }
                        }
                        //didn't find any inactive robots
                        //yield just in case we're low on bytecode

                    }
                    this.scancount = 0;
                }
                else
                {
                    this.scancount++;
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
        int exitcount = 0;
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
                this.currentstate = BUILDING_ON_MINE;
                return;
            }
            exitcount++;
            if(exitcount > 20)
            {
                this.currentstate = FORAGING;
                return;
            }
        }

    }

    private void buildingOnMineBehavior()
    {
        myRC.yield();
        try{
            if (myRC.getTeamResources() > Chassis.BUILDING.cost  &&
                this.builder.canBuild(UnitCommon.BASIC_BUILDING_CHASSIS,this.targetMine))
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
    }

    private void spottedInactiveRobotBehavior()
    {
        navigator.setDestination(targetUnit, false);
        while (true)
        {
            myRC.yield();

            if (!this.navigator.isAtDest())
            {
                navigator.doMovement();
            }
            else
            {
                this.currentstate = ACTIVATING_ROBOT;
                return;
            }
        }


    }

    private void activatingRobotBehavior()
    {
        try{
            myRC.yield();
            if(myRC.getTeamResources()>100 && myRC.getLocation().distanceSquaredTo(targetUnit)==1)
            {
                GameObject object = this.sensor.senseObjectAtLocation(
                    this.targetUnit,
                    RobotLevel.ON_GROUND
                    );

                //check if robot is still there
                if( object != null && object instanceof Robot)
                {

                    RobotInfo rinfo = this.sensor.senseRobotInfo((Robot)object);
                    if(!rinfo.on)
                    {
                        myRC.turnOn(targetUnit, RobotLevel.ON_GROUND);
                    }

                }
            }

        }
        catch( Exception ex)
        {
            ex.printStackTrace();
        }

        this.targetUnit = null;
        this.currentstate = FORAGING;
    }

    public void runBehavior()
    {
        while (true)
        {

            switch (this.currentstate)
            {
            case BUILDING_FACTORY:
                buildingFactoryBehavior();
                break;
            case FORAGING:
                forageBehavior();
                break;
            case SPOTTED_MINE:
                spottedMineBehavior();
                break;
            case BUILDING_ON_MINE:
                buildingOnMineBehavior();
                break;
            case SPOTTED_INACTIVE_ROBOT:
                spottedInactiveRobotBehavior();
                break;
            case ACTIVATING_ROBOT:
                activatingRobotBehavior();
                break;
            default:
                this.myRC.yield();
                break;
            }
            doIdleTasks();
        }

    }
}
