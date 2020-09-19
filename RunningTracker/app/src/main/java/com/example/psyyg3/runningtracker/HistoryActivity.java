package com.example.psyyg3.runningtracker;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("date");
        categories.add("start time");
        categories.add("end time");
        categories.add("distance");
        categories.add("speed");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        queryHistory(null);
        //use contentObserver to change the listView when a new recipe is added
        getContentResolver().
                registerContentObserver(
                        HistoryProviderContract.HISTORY_URI,
                        true,
                        new ChangeObserver(h));
    }

    class ChangeObserver extends ContentObserver {
        public ChangeObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryHistory(null);
        }
    }

    public void queryHistory(String sortOrder) {
        String[] projection = new String[]{
                HistoryProviderContract.ID,
                HistoryProviderContract.DATE,
                HistoryProviderContract.STARTTIME,
                HistoryProviderContract.ENDTIME,
                HistoryProviderContract.DISTANCE,
                HistoryProviderContract.SPEED
        };

        //the column name that will show in ListVIew
        String[] from = new String[]{
                HistoryProviderContract.ID,
                HistoryProviderContract.DATE,
                HistoryProviderContract.STARTTIME,
                HistoryProviderContract.ENDTIME,
                HistoryProviderContract.DISTANCE,
                HistoryProviderContract.SPEED
        };

        //the place to set the retrieved content
        int[] to = new int[]{
                R.id.idView,
                R.id.dateView,
                R.id.startTimeView,
                R.id.endTimeView,
                R.id.distanceView,
                R.id.speedView
        };

        Cursor cursor = getContentResolver().query(HistoryProviderContract.HISTORY_URI, projection, null, null, sortOrder);

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.item_layout,
                cursor,
                from,
                to,
                0);

        final ListView listView = findViewById(R.id.historyList);
        listView.setAdapter(dataAdapter);
        //shows the detail of a running history when user clicks it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int position,
                                    long id) {
                View v = listView.getChildAt(position);
                TextView idView = v.findViewById(R.id.idView);
                Bundle bundle = new Bundle();
                bundle.putString("id", idView.getText().toString());
                Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
    }

    //the function that goes to GraphActivity
    public void showGraph(View v){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(pos).toString();
        switch (item){
            case "date":
                queryHistory(HistoryProviderContract.DATE);
                break;
            case "start time":
                queryHistory(HistoryProviderContract.STARTTIME);
                break;
            case "end time":
                queryHistory(HistoryProviderContract.ENDTIME);
                break;
            case "distance":
                queryHistory(HistoryProviderContract.DISTANCE);
                break;
            case "speed":
                queryHistory(HistoryProviderContract.SPEED);
                break;
            default:
                queryHistory(HistoryProviderContract.ID);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d("g53mdp", "HistoryActivity onPause");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("g53mdp", "HistoryActivity onResume");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d("g53mdp", "HistoryActivity onStart");
        queryHistory(null); //change the listView when HistoryActivity restart
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d("g53mdp", "HistoryActivity onStop");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("g53mdp", "HistoryActivity onDestroy");
    }
}
