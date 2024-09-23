package com.mujahid.mobileecg;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.text.DecimalFormat;

/**
 * Created by killer on 10/22/16.
 */

public class Measurement extends Activity {

    static final int HISTORY_SIZE = 200;
    static SimpleXYSeries mSeries = null;
    static SimpleXYSeries mSeries1 = null;
    private XYPlot plot = null;
    private Redrawer redrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measurement_layout);
        initGraph();

    }

    private void initGraph() {
        plot = (XYPlot) findViewById(R.id.plot);

        mSeries = new SimpleXYSeries("R");
        mSeries.useImplicitXVals();
        mSeries1 = new SimpleXYSeries("L");
        mSeries1.useImplicitXVals();
        plot.setRangeBoundaries(-10000, 10000, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        plot.addSeries(mSeries,new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
        plot.addSeries(mSeries1, new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
        plot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        plot.setDomainStepValue(HISTORY_SIZE/10);
        // plot.setLinesPerRangeLabel(3);
        plot.setDomainLabel("Domain");
        plot.getDomainTitle().pack();
        plot.setRangeLabel("Range");
        plot.getRangeTitle().pack();
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#"));

        redrawer = new Redrawer(plot,100,true);
        PanZoom.attach(plot);

    }

    @Override
    public void onResume() {
        redrawer.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        redrawer.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        super.onDestroy();
    }
}
