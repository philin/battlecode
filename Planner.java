package team046;

import java.util.LinkedList;
import battlecode.common.*;

public abstract class Planner{

    protected Module navModule,mappingModule,commModule;
    protected LinkedList<IdleTask> tasks = new LinkedList<IdleTask>();
    protected int maxBytecodes = GameConstants.BYTECODE_LIMIT_BASE;
    //TODO, find out how many it actually takes
    private static final int DO_IDLE_OVERHEAD = 100;
    public void init(){
        if(navModule!=null){
            navModule.init(this);
        }
        if(mappingModule!=null){
            mappingModule.init(this);
        }
        if(commModule!=null){
            commModule.init(this);
        }
    }

    public Module getNavModule(){
        return navModule;
    }
    public Module getMappingModule(){
        return mappingModule;
    }
    public Module getCommModule(){
        return commModule;
    }
    //Add other module getters here

    public void addIdleTask(IdleTask task){
        tasks.offer(task);
    }
    public void doIdleTask(){
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