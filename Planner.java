package team046;

import java.util.LinkedList;
import battlecode.common.*;

public abstract class Planner{

    protected Module[] modules = new Module[ModuleType.NUM_OF_TYPES];
    protected LinkedList<IdleTask> tasks = new LinkedList<IdleTask>();
    protected int maxBytecodes = GameConstants.BYTECODE_LIMIT_BASE;
    //TODO, find out how many it actually takes
    private static final int DO_IDLE_OVERHEAD = 100;
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

    public void addIdleTask(IdleTask task){
        tasks.offer(task);
    }

    public void doIdleTasks(){
        while(maxBytecodes-Clock.getBytecodeNum()>500+DO_IDLE_OVERHEAD){
            IdleTask currTask = tasks.peek();
            if(currTask==null){
                break;
            }
            if(currTask.executeTask()){
                tasks.remove();
            }
        }
    }
}