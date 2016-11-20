package com.example.yuras.thirdapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class WifiMapActivity extends AppCompatActivity {
    private final static int CANVAS_HEIGHT = 1000;
    private final static int CANVAS_WIDTH = 1000;

    private ImageView imageView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.iv_wifi_map);

        bitmap = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawARGB(80, 102, 204, 255);

        ArrayList<Double> coordsMax = WifiPoints.getMaxWifiLevelCoords(getIntent()
                .getExtras()
                .getString("ssid"));
        ArrayList<Double> coordsMin = WifiPoints.getMinWifiLevelCoords(getIntent()
                .getExtras()
                .getString("ssid"));

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setShader(new LinearGradient(coordsMax.get(0).intValue(),
                coordsMax.get(1).intValue(),
                coordsMin.get(0).intValue(),
                coordsMin.get(1).intValue(),
                Color.GREEN, Color.RED, Shader.TileMode.CLAMP));
        paint.setStrokeWidth(4);

        ArrayList<Double> xs = Trajectory.getCoordsX();
        ArrayList<Double> ys = Trajectory.getCoordsY();

        Path path = new Path();
        path.moveTo(xs.get(0).floatValue(), ys.get(0).floatValue());
        for (int i = 1; i < xs.size(); i++) {
            path.lineTo(xs.get(i).floatValue(), ys.get(i).floatValue());
        }
        path.close();

        canvas.drawPath(path, paint);
        imageView.setImageBitmap(bitmap);
    }
}
