package team046.units;

import java.util.Random;
import battlecode.common.*;
import static battlecode.common.GameConstants.*;
import team046.*;

public class BasicBuilding extends Unit
{
    //states
    private static final int SELF_UPGRADE = 0;
    private static final int SCANNING = 1;
    private static final int ATTACKING = 2;
    private static final int SPAWNING_UNIT = 3;

    private static final int MAX_SPINS_PER_SCAN = 12;

    private int attackerBuildCountdown = 2;

    private int currentstate;
    private MapLocation targetlocation = null;
    private RobotLevel  targetlevel = null;

    private int scanspincount = 0;

    //controllers
    BuilderController builder = null;
    SensorController sensor = null;
    SensorController radarsensor = null;
    WeaponController weapon = null;
    MovementController motor = null;
    public BasicBuilding(RobotController myRC)
    {
        super(myRC);

        //initialize current state
        currentstate = 0;

        ComponentController [] components = myRC.components();
        for(int i = 0; i < components.length; ++i)
        {
            if (components[i].type() == ComponentType.RECYCLER)
            {
                builder = (BuilderController)components[i];
            }
            else if (components[i]  instanceof SensorController)
            {
                sensor = (SensorController)components[i];

            }
        }

    }

    public int getType()
    {
        return UnitCommon.BASIC_BUILDING;
    }

    private void selfUpgradeBehavior()
    {

        myRC.yield();
        try{
            Util.addComponent(myRC,
                              builder,
                              ComponentType.RADAR,
                              myRC.getLocation(),
                              RobotLevel.ON_GROUND);
            myRC.yield();

            Util.addComponent(myRC,
                              builder,
                              ComponentType.BLASTER,
                              myRC.getLocation(),
                              RobotLevel.ON_GROUND);

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        //identify the components
        for(ComponentController comp : myRC.components())
        {
            if(comp.type() == ComponentType.RADAR)
            {
                radarsensor = (SensorController)comp;
            }
            if(comp.type() == ComponentType.BLASTER)
            {
                weapon = (WeaponController)comp;
            }
            if(comp instanceof MovementController)
            {
                motor = (MovementController)comp;
            }
        }
        currentstate = SCANNING;
    }

    private void scanningBehavior()
    {

        while (true)
        {
            try{
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

                //rotate to the right
                Direction currdir = myRC.getDirection();
                Direction next = currdir.rotateRight();
                while(motor.isActive())
                {
                    myRC.yield();
                }
                motor.setDirection(next);
                scanspincount++;

                if(scanspincount > MAX_SPINS_PER_SCAN)
                {
                    //goto build state
                    scanspincount = 0;
                    currentstate = SPAWNING_UNIT;
                    return;
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
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
        currentstate = SCANNING;
        targetlocation = null;
        targetlevel = null;

    }

    private void spawningUnitBehavior()
    {

        myRC.yield();
        if(myRC.getTeamResources() > 300 )
        {

            MapLocation loc = myRC.getLocation();
            try{
                //find the first available tile

                if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                    loc.add(Direction.NORTH)))
                {
                    loc = loc.add(Direction.NORTH);
                }
                else if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                         loc.add(Direction.EAST)))
                {
                    loc = loc.add(Direction.EAST);
                }
                else if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                         loc.add(Direction.WEST)))
                {
                    loc = loc.add(Direction.WEST);
                }
                else if(builder.canBuild(UnitCommon.BASIC_BUILDER_CHASSIS,
                                         loc.add(Direction.SOUTH)))
                {
                    loc = loc.add(Direction.SOUTH);
                }
                else
                {
                    loc = null;
                }
                if (loc != null)
                {
                    if(attackerBuildCountdown == 0)
                    {
                        Util.buildUnit(myRC,
                                       builder,
                                       UnitCommon.BASIC_ATTACKER,
                                       loc);
                        attackerBuildCountdown = 3;
                    }
                    else
                    {
                        attackerBuildCountdown--;
                        Util.buildUnit(myRC,
                                       builder,
                                       UnitCommon.BASIC_BUILDER,
                                       loc);
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        currentstate = SCANNING;

    }

    public void runBehavior()
    {
        while (true)
        {
            switch (this.currentstate)
            {
            case SELF_UPGRADE:
                selfUpgradeBehavior();
                break;
            case SCANNING:
                scanningBehavior();
                break;
            case ATTACKING:
                attackingBehavior();
                break;
            case SPAWNING_UNIT:
                spawningUnitBehavior();
                break;
            default:
                myRC.yield();
                break;
            }
            doIdleTasks();
        }
    }
/*
  public void runBehavior()
  {
  armSelf();


  }
*/
}