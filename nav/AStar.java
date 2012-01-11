package team048.nav;

import battlecode.common.*;

import team048.Util;
import team048.util.*;
import team048.mapping.*;
import java.util.Comparator;

public class AStar implements PathPlanner{

    class NodeComparator implements Comparator<Map.Node>{
        public int compare(Map.Node a, Map.Node b){
            if(a.navInfo.f<b.navInfo.f){
                return -1;
            }
            else if(a.navInfo.f>b.navInfo.f){
                return 1;
            }
            return 0;
        }
    }
    private static final int MIN_BYTECODES=1000;
    private static final double SQRT2=Math.sqrt(2);
    Map map;
    Map.Node[][] nodes;
    Map.Node currBest;
    private FastPriorityQueue<Map.Node> openQueue = new FastPriorityQueue<Map.Node>(new NodeComparator());
    RobotController myRC;
    MapLocation dest;
    Map.Node destNode;
    boolean done;
    Direction[] path;
    Direction[] oldPath;

    private int stateCounter=0;

    public AStar(Map map, RobotController myRC){
        this.myRC=myRC;
        this.map=map;
    }

    void doNeighbor(Map.Node next, Map.Node old,Direction dir){
        if(next==null){
            return;
        }
        if(next.navInfo.state==-stateCounter){//if closed
            return;
        }
        double distance = (dir.isDiagonal()) ? SQRT2 : 1;
        double newG=old.navInfo.g+distance;
        if(next.navInfo.state==stateCounter){//if open
            if(next.navInfo.g>newG){
                next.navInfo.g=newG;
                next.navInfo.update(dest);
                next.navInfo.parent = old;
                next.navInfo.parentDir=dir;
                next.navInfo.length = old.navInfo.length+1;
                openQueue.update(next);
            }
        }
        else{ //if(next.navInfo.state!=-stateCounter){//if neither
            next.navInfo.state=stateCounter;
            next.navInfo.parent = old;
            next.navInfo.parentDir=dir;
            next.navInfo.length = old.navInfo.length+1;
            next.navInfo.g=newG;
            map.getTerrain(old.navInfo.location.add(dir));
            next.navInfo.location = old.navInfo.location.add(dir);
            next.navInfo.update(dest);
            openQueue.offer(next);
        }
    }

    void doNeighbors(Map.Node node){
        for(int i=0;i<8;i++){
            doNeighbor(node.neighbors[i],node,Util.intAsDirection(i));
        }
    }

    void movedForward(Direction dir){
        //currNode = currNode.getNeighbor(dir);
    }

    public void didTurn(Direction dir){}
    public void didMove(Direction dir){}
    public void gotStuck(int index){}

    public static final int MAX_STEPS = 30;

    public Direction[] planPath(MapLocation dest){
        stateCounter++;
        openQueue.clear();
        this.dest = dest;
        int steps = 0;
        Map.Node start = map.getCurrNode();
        Map.Node destNode = map.getNode(dest);
        start.navInfo.state=stateCounter;
        start.navInfo.parent=null;
        start.navInfo.parentDir=null;
        start.navInfo.length=0;
        start.navInfo.g=0;
        start.navInfo.location=myRC.getLocation();
        start.navInfo.update(dest);
        openQueue.offer(start);
        while(!openQueue.isEmpty()){
            Map.Node node = openQueue.poll();
            node.navInfo.state=-stateCounter;
            if(node!=destNode && steps<MAX_STEPS){
                doNeighbors(node);
            }
            else{
                //reconstruct the path
                Map.Node currNode = node;
                Direction[] path = new Direction[node.navInfo.length];
                int numSteps = 0;
                while(currNode.navInfo.length!=0){
                    path[currNode.navInfo.length-1]=currNode.navInfo.parentDir;
                    currNode = currNode.navInfo.parent;
                    numSteps++;
                }
                return path;
            }
            steps++;
        }
        return null;
    }

    private void reconstructPath(Map.Node node){
        path = new Direction[node.navInfo.length];
        while(node.navInfo.length!=0){
            path[node.navInfo.length-1]=node.navInfo.parentDir;
            node = node.navInfo.parent;
        }
    }

    public void doPlanning(int minSteps){
        if(done){
            return;
        }
        int currRound = Clock.getRoundNum();
        while(!openQueue.isEmpty() &&
              (minSteps>0 || (Clock.getBytecodesLeft()>MIN_BYTECODES &&
                              Clock.getRoundNum()==currRound))){
            Map.Node node = openQueue.poll();
            node.navInfo.state=-stateCounter;
            if(node!=destNode){
                doNeighbors(node);
            }
            else{
                done = true;
                //reconstruct the path
                reconstructPath(node);
            }
            minSteps--;
        }
    }

    public void setDest(MapLocation dest){
        this.dest = dest;
        destNode = map.getNode(dest);
        done = false;
    }

    public Direction[] getPath(){
        if(!done){
            if(openQueue.isEmpty()){
                path = null;
            }
            else{
                reconstructPath(openQueue.poll());
            }
        }
        oldPath = path;
        path = null;
        return oldPath;
    }
}

