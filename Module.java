package team048;

public interface Module{
    public void init(Planner planner);
    public ModuleType getType();
    //note this is really hacky right now, every module should implement it but
    //we only use it for nav, in theory we could also use it for other modules
    public void doIdleTasks();
}