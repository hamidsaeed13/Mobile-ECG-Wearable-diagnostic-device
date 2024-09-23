package com.mujahid.mobileecg;

import static com.mujahid.mobileecg.Measurement.HISTORY_SIZE;
import static com.mujahid.mobileecg.Measurement.mSeries;
import static com.mujahid.mobileecg.Measurement.mSeries1;

/**
 * Created by killer on 10/5/16.
 */

public class DataAppend implements Runnable {
    static String message;
    private String[] seline;
    private String[] selnum;

    public DataAppend()
    {

    }

    @Override
    public void run() {

    }
    public void AppendData(){
        try {
            seline = message.split("\n");
            int i = 0;
            while (i < seline.length)

            {
                if (seline[i].trim() == "") {
                    seline[i] = "  ";
                }
                selnum = seline[i].split(",");
                if (isInteger(selnum[0].trim())) {
                    int num = Integer.parseInt(selnum[0].trim());

                    if (mSeries.size() > HISTORY_SIZE) {
                        mSeries.removeFirst();
                        }
                        mSeries.addLast(null, num);
                    }
                if (isInteger(selnum[1].trim())) {
                    int num = Integer.parseInt(selnum[1].trim());

                    if (mSeries1.size() > HISTORY_SIZE) {
                        mSeries1.removeFirst();
                    }
                    mSeries1.addLast(null, num);
                }

                    i++;
                }
        }
        catch (Exception e) {

        }


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

}
