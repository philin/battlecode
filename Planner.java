package team048;

import java.util.LinkedList;
import battlecode.common.*;

public abstract class Planner{
    private static Planner currPlanner;
    protected RobotController myRC;
    protected Module[] modules = new Module[ModuleType.NUM_OF_TYPES];
    protected LinkedList<IdleTask> tasks = new LinkedList<IdleTask>();
    protected int maxBytecodes = GameConstants.BYTECODE_LIMIT;
    //TODO, find out how many it actually takes
    private static final int DO_IDLE_OVERHEAD = 100;

    public Planner(RobotController rc){
        currPlanner = this;
        myRC=rc;
    }

    public void init(){
        for(Module m : modules){
            if(m!=null){
                m.init(this);
            }
        }
    }

    protected void addModule(Module module){
        modules[module.getType().ordinal()]=module;
    }

    public Module getModule(ModuleType type){
        return modules[type.ordinal()];
    }

    public static void doYield(){
        int roundNum = Clock.getRoundNum();
        if(Clock.getBytecodesLeft()>100){
            Module nav = currPlanner.getModule(ModuleType.NAVIGATION);
            if(nav!=null){
                nav.doIdleTasks();
            }
        }
        if(Clock.getBytecodesLeft()>20 && roundNum==Clock.getRoundNum()){
            //if we have enough bytecodes to safely ensure that the yield
            //happens in the right round call yield
            currPlanner.myRC.yield();
        }
        else{
            //otherwise loop till the round finishes
            while(roundNum==Clock.getRoundNum()){
            }
        }
    }

    public void addIdleTask(IdleTask task){
        tasks.offer(task);
    }

    public void doIdleTasks(){
        int length = tasks.size();
        while(maxBytecodes-Clock.getBytecodeNum()>500+DO_IDLE_OVERHEAD && length>0){
            length--;
            IdleTask currTask = tasks.pop();
            if(currTask.executeTask()){
                tasks.offer(currTask);
            }
        }
    }

    public void receiveMessages(int moduleIds, Message message, int intOffset,
                                int mapLocationOffset,int stringOffset){
        //STUB
    }
}