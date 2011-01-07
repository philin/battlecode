package team046;

public abstract class Planner{

    protected Module navModule,mappingModule,commModule;

    public void init(){
        if(navModule!=null){
            navModule.init(this);
        }
        if(mappingModule!=
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