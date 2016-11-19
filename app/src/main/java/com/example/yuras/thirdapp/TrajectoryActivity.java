package com.example.yuras.thirdapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class TrajectoryActivity extends AppCompatActivity {
    private final static int LINE_LENGTH = 5;
    private final static int START_COORD_X = 250;
    private final static int START_COORD_Y = 250;
    private final static int CANVAS_HEIGHT = 1000;
    private final static int CANVAS_WIDTH = 1000;

    ImageView imageView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.iv);
        listView = (ListView) findViewById(R.id.list_view);

        ArrayList<String> directions_list = new ArrayList<>();
        directions_list.addAll(getIntent().getExtras().getStringArrayList("directions"));

        Bitmap bitmap = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(80, 102, 204, 255);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);

        double x = START_COORD_X, y = START_COORD_Y;
        double offsetX, offsetY;
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < directions_list.size(); i++) {
            if (i == 0) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.BLACK);
            }

            offsetX = LINE_LENGTH * Math.cos(Math.toRadians(Double.parseDouble(directions_list.get(i))));
            offsetY = LINE_LENGTH * Math.sin(Math.toRadians(Double.parseDouble(directions_list.get(i))));

            canvas.drawLine((float)x, (float)y, (float)(x + offsetX), (float)(y + offsetY), paint);

            x += offsetX;
            y += offsetY;

            arrayList.add(String.valueOf(Math.cos(Math.toRadians(Double.parseDouble(directions_list.get(i)))))
                    + "; " + String.valueOf(Math.sin(Math.toRadians(Double.parseDouble(directions_list.get(i)))))
                    + "; " + String.valueOf(directions_list.get(i)));
        }

        imageView.setImageBitmap(bitmap);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
    }

}
