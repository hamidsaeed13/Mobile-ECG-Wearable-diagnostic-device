package com.killer.mobileecg;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.androidplot.xy.SimpleXYSeries;

import java.util.NoSuchElementException;

import static com.killer.mobileecg.MainActivity.Buff;
import static com.killer.mobileecg.MainActivity.HISTORY_SIZE;
import static com.killer.mobileecg.MainActivity.mSeries;

/**
 * Created by killer on 10/5/16.
 */

public class DataAppend implements Runnable {
    private String message;
    private String[] seline;



    public DataAppend ()
    {

    }

    @Override
    public void run() {
        while (true) {
            message = Buff.read();
            if (message!=null)
            {
                try {
                    seline = message.split("\n");
                    int i = 0;
                    while (i < seline.length)

                    {
                        if (seline[i].trim() == "") {
                            seline[i] = "0";
                        }
                        if (isInteger(seline[i].trim())) {


                            int num = Integer.parseInt(seline[i].trim());

                            if (mSeries.size() > HISTORY_SIZE) {
                                mSeries.removeFirst();

                            }
                            mSeries.addLast(null, num);
                        }

                        i++;
                    }
                } catch (Exception e) {

                }
            }
        }
    }
    public void AppendData(){

    }


    public boolean isInteger( String input )
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }




    //Buffer Calss
}
class CircularBuffer<T> {
    // internal data storage
    private T[] data;
    // indices for inserting and removing from queue
    private int front = 0;
    private int insertLocation = 0;
    // number of elements in queue
    private int size = 0;
    /**
     * Creates a circular buffer with the specified size.
     *
     * @param bufferSize
     *      - the maximum size of the buffer
     */
    public CircularBuffer(int bufferSize) {
        data = (T[]) new Object[bufferSize];
    }
    /**
     * Inserts an item at the end of the queue. If the queue is full, the oldest
     * value will be removed and head of the queue will become the second oldest
     * value.
     *
     * @param item
     *      - the item to be inserted
     */
    public synchronized void store(T item) {
        data[insertLocation] = item;
        insertLocation = (insertLocation + 1) % data.length;
        /**
         * If the queue is full, this means we just overwrote the front of the
         * queue. So increment the front location.
         */
        if (size == data.length) {
            front = (front + 1) % data.length;
        } else {
            size++;
        }
    }
    /**
     * Returns the number of elements in the buffer
     *
     * @return int - the number of elements inside this buffer
     */
    public synchronized int size() {
        return size;
    }
    /**
     * Returns the head element of the queue.
     *
     * @return T
     */
    public synchronized T read() {
        if (size == 0) {
            return null;
        }
        T retValue = data[front];
        front = (front + 1) % data.length;
        size--;
        return retValue;
    }
    /**
     * Returns the head of the queue but does not remove it.
     *
     * @return
     */
    public synchronized T peekFront() {
        if (size == 0) {
            return null;
        } else {
            return data[front];
        }
    }
    /**
     * Returns the last element of the queue but does not remove it.
     *
     * @return T - the most recently added value
     */
    public synchronized T peekLast() {
        if (size == 0) {
            return null;
        } else {
            int lastElement = insertLocation - 1;
            if (lastElement < 0) {
                lastElement = data.length - 1;
            }
            return data[lastElement];
        }
    }
}