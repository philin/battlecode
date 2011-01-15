package team046.comm;

import battlecode.common.*;

interface MessageData{
    public int getModuleFlags();
    public int getIntCount();
    public int getMapLocationCount();
    public int getStringCount();
    public int writeInts(int[] ints, int offset);
    public int writeMapLocations(MapLocation[] locations, int offset);
    public int writeStrings(String[] strings, int offset);
}