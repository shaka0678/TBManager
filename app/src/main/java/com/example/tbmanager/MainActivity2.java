package com.example.tbmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.widget.ImageView;

import java.time.Instant;

public class MainActivity2 extends AppCompatActivity {

           CardView cardViewtotal;
    CardView cardViewpat;
    CardView cardViewadd;
    CardView cardViewmap;
    ImageButton imageButtonNOT;
    CardView cardViewgeo;
    CardView cardViewsettings;
    CardView cardViewinfo;
    GraphView graphView;
    ImageButton imageButtondown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        cardViewtotal = findViewById(R.id.card1);
        cardViewadd = findViewById(R.id.appointCard);
        cardViewpat = findViewById(R.id.patientCard);
        cardViewmap = findViewById(R.id.aCard);
        cardViewsettings = findViewById(R.id.set24);
        cardViewinfo = findViewById(R.id.sysinfo);
        cardViewgeo = findViewById(R.id.ageofence);
        imageButtonNOT = findViewById(R.id.imgbtn0);
        graphView = findViewById(R.id.idGraphView);

        // on below line we are adding data to our graph view.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.
                new DataPoint(0, 1),
                new DataPoint(1, 3),
                new DataPoint(2, 4),
                new DataPoint(3, 9),
                new DataPoint(4, 6),
                new DataPoint(5, 3),
                new DataPoint(6, 6),
                new DataPoint(7, 1),
                new DataPoint(8, 2)
        });

        // after adding data to our line graph series.
        // on below line we are setting 
        // title for our graph view.
        graphView.setTitle("My Graph View");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.black);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(14);

        // on below line we are adding
        // data series to our graph view.
        graphView.addSeries(series);


        imageButtonNOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this,devices.class);
                startActivity(intent);
            }
        });



        cardViewadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Intent intent = new Intent(getApplicationContext(), MainActivity6.class);
                 startActivity(intent);
            }
        });

        cardViewgeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), geopage.class);
                startActivity(intent);
            }
        });
        cardViewinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
                startActivity(intent);
            }
        });
        cardViewsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity5.class);
                startActivity(intent);
            }
        });

        cardViewpat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(),MainActivity3.class);
                startActivity(intent);

            }
        });

        cardViewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);

            }
        });

    }
}