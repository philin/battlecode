package team338;

import battlecode.common.*;

class Behavior
{
    public Behavior(BehaviorType t, Object[] s)
    {
        type = t;
        state = s;
    }

    public enum BehaviorType
    {
        MOBILE_DEFEND_TERRITORY,
        MOBILE_CREATE_TERRITORY,
        MOBILE_ATTACK_UNIT,
        WOUT_COLLECT_FLUX;
    }

    public Object[] state;
    public BehaviorType type;
}