package team046.comm;

import java.util.LinkedList;
import battlecode.common.*;
import team046.*;


public class Communicator implements Module{
    BroadcastController broadcaster;
    RobotController myRC;
    Planner planner;
    LinkedList<MessageData> messages;
    int intCount;
    int mapLocationCount;
    int stringCount;
    Message currMessage;

    private static final int MESSAGE_OVERHEAD=1;

    public Communicator(RobotController rc, BroadcastController broadcaster){
        this.myRC=rc;
        this.broadcaster = broadcaster;
    }

    public void addMessage(MessageData data){
        messages.add(data);
        intCount+=data.getIntCount()+MESSAGE_OVERHEAD;
        mapLocationCount+=data.getMapLocationCount();
        stringCount+=data.getStringCount();
    }

    //message format
    //integer data
    //message count
    //[module_ids(8bits)|int_message_length(8bits)|map_location_length(8bits)|string_length(8bits)]
    //data
    public void sendMessage(){
        int intOffset=0;
        int mapLocationOffset=0;
        int stringOffset=0;

        int messageCount=messages.size();
        currMessage.ints = new int[intCount+1];
        currMessage.locations = new MapLocation[mapLocationCount];
        currMessage.strings = new String[stringCount];
        intOffset++;
        if(messageCount>0){
            MessageData data;
            do{
                data = messages.pop();
                int flags = data.getModuleFlags()<<24;

                int currIntLength=data.writeInts(currMessage.ints,intOffset);
                int currMapLocationLength
                    =data.writeMapLocations(currMessage.locations,mapLocationOffset);
                int currStringLength
                    =data.writeStrings(currMessage.strings,stringOffset);
                flags|=(currIntLength<<16) | (currMapLocationLength<<8) | currStringLength;
                intOffset+=currIntLength;
                mapLocationOffset+=currMapLocationLength;
                stringOffset+=currStringLength;
            }while(!messages.isEmpty());
        }
        currMessage.ints[0]=currMessage.ints.length;
        try{
            broadcaster.broadcast(currMessage);
        }
        catch(GameActionException e){
            //bad stuff
        }
    }

    public void processMessage(Message message){
        int intOffset=0;
        int mapLocationOffset=0;
        int stringOffset=0;
        if(message.ints[intOffset]!=message.ints.length){
            return;//bad message
        }
        intOffset++;
        int messageCount = message.ints[intCount];
        intOffset++;
        for(int i=0;i<messageCount;i++){
            int flags = message.ints[intOffset];
            int module_ids = flags>>24&0xff;
            int intLength = (flags>>16)&0xff;
            int mapLocationLength = (flags>>8)&0xff;
            int stringLength = flags&0xff;
            intOffset++;
            planner.receiveMessages(module_ids,message,intOffset,mapLocationOffset,stringOffset);
            intOffset+=intLength;
            mapLocationOffset+=mapLocationLength;
            stringOffset+=stringLength;
        }
    }

    public void receiveMessage(){
        Message[] newMessages = myRC.getAllMessages();
        for(Message m : newMessages){
            processMessage(m);
        }
    }

    public ModuleType getType(){
        return ModuleType.COMMUNICATION;
    }

    public void init(Planner planner){
        this.planner = planner;
        currMessage = new Message();
    }
}