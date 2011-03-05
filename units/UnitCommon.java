package team046.units;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public final class UnitCommon
{
    /** unit type values **/
    public static final int BASIC_BUILDER = 0;
    public static final int BASIC_BUILDING = 1;
    public static final int BASIC_ATTACKER = 2;
    public static final int FACTORY = 3;
    public static final int SWARM_LEADER = 4;

    /** unit construction definitions **/
    public static final Chassis BASIC_BUILDER_CHASSIS = Chassis.LIGHT;
    public static final RobotLevel BASIC_BUILDER_HEIGHT = RobotLevel.ON_GROUND;
    public static final ComponentType[] BASIC_BUILDER_COMPONENTS = { ComponentType.PROCESSOR,
                                                                     ComponentType.CONSTRUCTOR,
                                                                     ComponentType.SIGHT };


    public static final Chassis BASIC_BUILDING_CHASSIS = Chassis.BUILDING;
    public static final RobotLevel BASIC_BUILDING_HEIGHT = RobotLevel.ON_GROUND;
    public static final ComponentType[] BASIC_BUILDING_COMPONENTS = { ComponentType.RECYCLER};


    public static final Chassis BASIC_ATTACKER_CHASSIS = Chassis.LIGHT;
    public static final RobotLevel BASIC_ATTACKER_HEIGHT = RobotLevel.ON_GROUND;
    public static final ComponentType[] BASIC_ATTACKER_COMPONENTS = { ComponentType.PROCESSOR,
                                                                      ComponentType.BLASTER,
                                                                      ComponentType.RADAR };


    public static final Chassis FACTORY_CHASSIS = Chassis.BUILDING;
    public static final RobotLevel FACTORY_HEIGHT = RobotLevel.ON_GROUND;
    public static final ComponentType[] FACTORY_COMPONENTS = { ComponentType.FACTORY };


    //total cost: 60+46+14+13=133
    public static final Chassis SWARM_LEADER_CHASSIS = Chassis.HEAVY;
    public static final RobotLevel SWARM_LEADER_HEIGHT = RobotLevel.ON_GROUND;
    public static final ComponentType[] SWARM_LEADER_COMPONENTS = { ComponentType.HARDENED,
                                                                    ComponentType.HARDENED,
                                                                    ComponentType.DISH,
                                                                    ComponentType.MEDIC };


}