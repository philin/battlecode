package team046.nav;

import battlecode.common.*;

import team046.Util;
import team046.mapping.*;
import java.util.PriorityQueue;
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

    private static final double SQRT2=Math.sqrt(2);
    Map map;
    Map.Node[][] nodes;
    Map.Node currBest;
    private PriorityQueue<Map.Node> openQueue = new PriorityQueue<Map.Node>(10,new NodeComparator());
    RobotController myRC;
    MapLocation dest;

    private int stateCounter=0;

    public AStar(Map map, RobotController myRC){
        this.myRC=myRC;
        this.map=map;
    }

    void doNeighbor(Map.Node next, Map.Node old,Direction dir){
        double distance = (dir.isDiagonal()) ? SQRT2 : 1;
        if(next==null){
            return;
        }
        if(next.navInfo.state==-stateCounter){
            return;
        }
        double newG=old.navInfo.g+distance;
        if(next.navInfo.state==stateCounter){//if open
            if(next.navInfo.g>newG){
                openQueue.remove(next);
                next.navInfo.g=newG;
                next.navInfo.update(dest);
                next.navInfo.parent = old;
                next.navInfo.parentDir=dir;
                next.navInfo.length = old.navInfo.length+1;
                openQueue.offer(next);
            }
        }
        else if(next.navInfo.state!=-stateCounter){//if neither
            next.navInfo.state=stateCounter;
            next.navInfo.parent = old;
            next.navInfo.parentDir=dir;
            next.navInfo.length = old.navInfo.length+1;
            next.navInfo.g=newG;
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

    public static final int MAX_STEPS = 20;

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

                while(currNode.navInfo.length!=0){
                    path[currNode.navInfo.length-1]=currNode.navInfo.parentDir;
                    currNode = currNode.navInfo.parent;
                }
                return path;
            }
            steps++;
        }
        return null;
    }
}

