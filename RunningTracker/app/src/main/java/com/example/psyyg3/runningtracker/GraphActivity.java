package com.example.psyyg3.runningtracker;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    LineGraphSeries<DataPoint> series = null;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        addData();
        graph.addSeries(series);

        // set date label
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); //set the number of vertical labels
        graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (m)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("date");

        //set graph
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling

        // styling series
        series.setTitle("Running history");
        series.setColor(Color.BLUE);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
    }

    // the function that sets the TextView with a track history information
    public void addData() {

        String[] projection = new String[]{
                HistoryProviderContract.ID,
                HistoryProviderContract.DATE,
                HistoryProviderContract.STARTTIME,
                HistoryProviderContract.ENDTIME,
                HistoryProviderContract.DISTANCE,
                HistoryProviderContract.SPEED
        };

        Cursor cursor = getContentResolver().query(HistoryProviderContract.HISTORY_URI, projection,
                null, null, HistoryProviderContract.DATE);
        if(cursor.moveToFirst()){
            Date date = null;
            float distance = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            do{
                try {
                    Date newDate = dateFormat.parse(cursor.getString(1));
                    if(date == null){ //if is the first day
                        date = newDate;
                        graph.getViewport().setMinX(date.getTime());
                        distance = Float.valueOf(cursor.getString(4));
                    }
                    else if(newDate.getTime() == date.getTime()){ //add distance on the same day
                        distance = distance + Float.valueOf(cursor.getString(4));
                    }
                    else{ //current date is different from previous date
                        series.appendData(new DataPoint(date, distance),true, 40); //add previous day's data
                        date = newDate;
                        distance = Float.valueOf(cursor.getString(4));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }while(cursor.moveToNext());
            graph.getViewport().setMaxX(date.getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHumanRounding(false);
            series.appendData(new DataPoint(date, distance),true, 40);
        }
    }
}
