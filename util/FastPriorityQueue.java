package team046.util;

import java.util.Comparator;
import java.util.Arrays;

public class FastPriorityQueue<T>{
    //in the ultimate irony, without reflections, I can't be typesafe
    Object[] heapData;
    int size;
    Comparator<T> comparator;
    public FastPriorityQueue(Comparator<T> comparator){
        this.comparator = comparator;
        size=0;
        heapData = new Object[(1<<8)-1];;
    }

    private void heapifyUp(int loc){
        T value = (T)heapData[loc];
        while(loc>0){
            int parent = (loc-1)>>1;
            if(comparator.compare((T)heapData[parent],value)>0){
                heapData[loc]=heapData[parent];
                loc = parent;
            }
            else{
                heapData[loc]=value;
                break;
            }
        }
    }
    private void heapifyDown(int loc){
        T value = (T)heapData[loc];
        while(loc<size){
            int left = ((loc+1)<<1)-1;
            int right = (loc+1)<<1;
            if(right<size){
                if(comparator.compare((T)heapData[right],value)<0){
                    if(comparator.compare((T)heapData[left],(T)heapData[right])<0){
                        //go left
                        heapData[loc]=heapData[left];
                        loc=left;
                    }
                    else{
                        //go right
                        heapData[loc]=heapData[right];
                        loc=right;
                    }
                }
                else if(comparator.compare((T)heapData[left],value)<0){
                    //go left
                    heapData[loc]=heapData[left];
                    loc=left;
                }
                else{
                    heapData[loc]=value;
                    break;
                }
            }
            else if(left<size && comparator.compare((T)heapData[left],value)<0){
                //go left
                heapData[loc]=heapData[left];
                loc=left;
            }
            else{
                heapData[loc]=value;
                break;
            }
        }
    }

    public void offer(T t){
        if(size==heapData.length){
            heapData = Arrays.copyOf(heapData,(heapData.length+1)*2-1);
        }
        heapData[size]=t;
        heapifyUp(size);
        size++;
    }
    public T poll(){
        Object temp = heapData[size-1];
        heapData[size-1] = heapData[0];
        heapData[0] = temp;
        size--;
        heapifyDown(0);
        return (T)heapData[size];
    }

    public void update(T t){
        for(int i=0;i<size;i++){
            if(heapData[i]==t){
                heapifyUp(i);
                heapifyDown(i);
                break;
            }
        }
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public void clear(){
        size=0;
    }
}