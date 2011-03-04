package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;

import team046.mapping.*;
import team046.nav.*;
import team046.*;

public class BasicAttacker extends Unit
{
        private int scancount = 0;
    private static final int SCAN_DELAY = 10;

    //controllers
    WeaponController weapon = null;
    MovementController motor = null;
    SensorController radarsensor = null;

    Map map = null;
    Navigator navigator = null;
    Random rand = null;


    //states
    private static final int HUNTING = 0;
    private static final int ATTACKING = 1;

    private int currentstate = 0; //default to foraging behavior
    private MapLocation targetlocation = null;
    private RobotLevel targetlevel = null;

    public BasicAttacker(RobotController myRC)
    {
        super(myRC);
        ComponentController [] components = this.myRC.components();

        for(int i = 0; i < components.length; ++i)
        {
            if(components[i].type() == ComponentType.BLASTER)
            {
                this.weapon = (WeaponController)components[i];
            }
            else if(components[i] instanceof MovementController)
            {
                this.motor = (MovementController)components[i];
            }
            else if(components[i].type() == ComponentType.RADAR)
            {
                this.radarsensor = (SensorController)components[i];
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
        return UnitCommon.BASIC_ATTACKER;
    }

    private void huntingBehavior()
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

                    myRC.yield();
                    GameObject[] objects = radarsensor.senseNearbyGameObjects(GameObject.class);
                    for(GameObject go : objects)
                    {
                        if(go instanceof Mine)
                        {
                            continue;
                        }
                        MapLocation targLoc = radarsensor.senseLocationOf(go);
                        if(go.getTeam() != myRC.getTeam() && weapon.withinRange(targLoc))
                        {
                            //if non-team
                            currentstate = ATTACKING;
                            targetlevel = go.getRobotLevel();
                            targetlocation = targLoc;
                            return;
                        }
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


    private void attackingBehavior()
    {

        /*
          make sure there are no yields between the first attack
          and the detecting of the enemy

          keep attacking same position until there is nothing left there to attack
        */
        while(weapon.isActive())
        {
            myRC.yield();
        }

        while( true )
        {
            try{
                //shoot the target
                weapon.attackSquare(targetlocation, targetlevel);

                //check occupancy of that square again
                GameObject occupier = radarsensor.senseObjectAtLocation(targetlocation,
                                                                    targetlevel);

                if(occupier == null
                   || occupier != null
                   && occupier.getTeam() == myRC.getTeam())
                {
                    break;
                }

                while(weapon.isActive())
                {
                    myRC.yield();
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            //yield at the end only

        }
        //reset the variables
        currentstate = HUNTING;
        targetlocation = null;
        targetlevel = null;

    }

    public void runBehavior()
    {
        while (true)
        {

            switch (this.currentstate)
            {
            case HUNTING:
                huntingBehavior();
                break;
            case ATTACKING:
                attackingBehavior();
                break;
            default:
                this.myRC.yield();
                break;
            }
            doIdleTasks();
        }

    }
}