package team046;

public abstract class Planner{

    private Module navModule,mappingModule,commModule;

    public void init(){
        navModule.init(this);
        mappingModule.init(this);
        commModule.init(this);
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
}