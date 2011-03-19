package team046.units;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;
import team046.*;

public class Factory extends Unit
{

    protected static final int MAX_SPINS_PER_SCAN = 12;

    //states
    protected static final int SCANNING = 0;
    protected static final int ATTACKING = 1;
    protected static final int SPAWNING_UNIT = 2;

    protected int attackerBuildCountdown = 2;

    protected int currentstate;


    //controllers
    protected BuilderController builder = null;
    protected SensorController sensor = null;
    protected SensorController sightsensor = null;
    protected MovementController motor = null;
    protected WeaponController smg0 = null;
    protected WeaponController smg1 = null;

    private boolean hassmg = false;

    protected MapLocation targetlocation = null;
    protected RobotLevel  targetlevel = null;

    protected int scanspincount = 0;

    public Factory(RobotController myRC)
    {
        super(myRC);

        //initialize current state
        currentstate = 0;

        ComponentController [] components = myRC.components();
        for(int i = 0; i < components.length; ++i)
        {
            if (components[i] instanceof BuilderController)
            {
                builder = (BuilderController)components[i];
            }
            else if (components[i]  instanceof SensorController)
            {
                sensor = (SensorController)components[i];

            }
            else if (components[i] instanceof MovementController)
            {
                motor = (MovementController)components[i];
            }
        }

    }

    public int getType()
    {
        return UnitCommon.FACTORY;
    }


    /*
      returns true if there are two smgs armed
     */

    private boolean isArmed()
    {
        int smgcount = 0;
        ComponentController [] components = myRC.components();
        for(int i = 0; i < components.length; ++i)
        {
            if(components[i].type() == ComponentType.SMG)
                smgcount++;
        }
        if(smgcount == 2)
            return true;

        return false;
    }

    private void loadWeaponPointers()
    {
        int smgcount = 0;
        ComponentController [] components = myRC.components();
        for(int i = 0; i < components.length; ++i)
        {
            if(components[i].type() == ComponentType.SIGHT)
            {
                sightsensor = (SensorController)components[i];
            }
            if(components[i].type() == ComponentType.SMG)
            {
                if(smgcount == 0)
                {
                    smg0 = (WeaponController)components[i];
                }
                else
                {
                    smg1 = (WeaponController)components[i];
                }
                smgcount++;
            }
        }

    }

    protected void scanningBehavior()
    {
        if(!hassmg && !isArmed())
        {
            currentstate = SPAWNING_UNIT;
            return;
        }
        else if(!hassmg && isArmed())
        {
            loadWeaponPointers();
            hassmg = true;
        }
        while (true)
        {
            try{
                doYield();
                GameObject[] objects = sightsensor.senseNearbyGameObjects(GameObject.class);
                for(GameObject go : objects)
                {
                    if(go instanceof Mine)
                    {
                        continue;
                    }

                    MapLocation targLoc = sightsensor.senseLocationOf(go);
                    if(go.getTeam() != myRC.getTeam() && smg0.withinRange(targLoc))
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
                    doYield();
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


    protected void attackingBehavior()
    {

        /*
          make sure there are no yields between the first attack
          and the detecting of the enemy

          keep attacking same position until there is nothing left there to attack
        */

        while(smg0.isActive() || smg1.isActive())
        {
            doYield();
        }

        while( true )
        {
            try{
                //shoot the target
                WeaponController weapon = null;
                if(!smg0.isActive())
                {
                    weapon = smg0;
                }
                else if(!smg1.isActive())
                {
                    weapon = smg1;
                }
                if(weapon != null)
                {
                    weapon.attackSquare(targetlocation, targetlevel);

                    //check occupancy of that square again
                    GameObject occupier = sightsensor.senseObjectAtLocation(
                        targetlocation,
                        targetlevel);

                    if(occupier == null
                       || occupier != null
                       && occupier.getTeam() == myRC.getTeam())
                    {
                        break;
                    }
                }
                while(smg0.isActive() || smg1.isActive())
                {
                    doYield();
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

    protected void spawningUnitBehavior()
    {

        doYield();
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
/*                    if(attackerBuildCountdown == 0)
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
                                       }*/
                    // System.out.println("building swarm leader");
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
                doYield();
                break;
            }
        }
    }

}