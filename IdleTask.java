package team046;

public interface IdleTask{
    //MUST NOT take more than 500 bytecodes
    //return true if the task is complete
    public boolean executeTask();
}