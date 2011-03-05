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


}