package team046.UnitTypes;

public abstract class Unit
{
    protected int type;
    //basic unit function headers go here
    public Unit(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return this.type;
    }
}