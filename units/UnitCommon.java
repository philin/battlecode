package team046.units;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public final class UnitCommon
{
    /** unit type values **/
    public static final int BASIC_BUILDER = 0;
    public static final int HEAVY_ATTACKER = 1;

    public static final int BASIC_BUILDING = 2;

    /** unit construction definitions **/
    public static final Chassis BASIC_BUILDER_CHASSIS = Chassis.LIGHT;
    public static final RobotLevel BASIC_BUILDER_HEIGHT = RobotLevel.ON_GROUND;
    public static final ComponentType[] BASIC_BUILDER_COMPONENTS = { ComponentType.CONSTRUCTOR, ComponentType.SIGHT };


}