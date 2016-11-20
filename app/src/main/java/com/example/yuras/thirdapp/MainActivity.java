package com.example.yuras.thirdapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static int LINE_LENGTH = 10;
    private final static int START_COORD_X = 250;
    private final static int START_COORD_Y = 250;
    private final static int CANVAS_HEIGHT = 1000;
    private final static int CANVAS_WIDTH = 1000;

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private static final int ORIENTATION_DELAY = 250;

    private WifiManager mainWifi;
    private WifiReceiver wifiScanReceiver;
    private SensorsHandler sensorsHandler;

    private Handler h;
    private Runnable scan;
    private Runnable orientation;

    private Button startBtn;
    private Button stopBtn;

    double x, y;
    double offsetX, offsetY;

    private Canvas canvas;
    private Paint paint;
    private ImageView imageView;
    private Bitmap bitmap;
    private Path path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        h = new Handler();

        bitmap = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawARGB(80, 102, 204, 255);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setShader(new LinearGradient(0, 0, 1000, 1000, Color.GREEN, Color.RED, Shader.TileMode.MIRROR));
        paint.setStrokeWidth(4);

        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        sensorsHandler = new SensorsHandler(this);

        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        imageView = (ImageView) findViewById(R.id.iv_main);

        wifiScanReceiver = new WifiReceiver();
        registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (!mainWifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        scan = new Runnable() {
            @Override
            public void run() {
                mainWifi.startScan();
                Toast.makeText(getApplicationContext(), "Scanning",
                        Toast.LENGTH_SHORT).show();
            }
        };

        path = new Path();
        path.moveTo(START_COORD_X, START_COORD_Y);
        x = START_COORD_X;
        y = START_COORD_Y;
        Trajectory.addCoords(x, y);
        orientation = new Runnable() {
            @Override
            public void run() {
                double angle = sensorsHandler.getAzimuth();

                offsetX = LINE_LENGTH * Math.cos(Math.toRadians(angle));
                offsetY = LINE_LENGTH * Math.sin(Math.toRadians(angle));

                canvas.drawLine((float)x, (float)y, (float)(x + offsetX), (float)(y + offsetY), paint);
                path.lineTo((float) (x + offsetX), (float) (y + offsetY));

                x += offsetX;
                y += offsetY;
                Trajectory.addCoords(x, y);

                imageView.setImageBitmap(bitmap);

                h.postDelayed(orientation, ORIENTATION_DELAY);
            }
        };
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        mainWifi.startScan();
        Toast.makeText(getApplicationContext(), "Scanning",
                Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        unregisterReceiver(wifiScanReceiver);
        h.removeCallbacks(scan);
        h.removeCallbacks(orientation);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResults = mainWifi.getScanResults();

                WifiPoints.add(new WifiPointInfo(scanResults, x, y));

                Toast.makeText(getApplicationContext(), "Received",
                        Toast.LENGTH_SHORT).show();

                h.post(scan);
            } else {
                Toast.makeText(getApplicationContext(), "Cannot receive any wifi",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), mainWifi.getScanResults().get(0).SSID,
                    Toast.LENGTH_SHORT).show();

        }
    }

    public void gotoSensors(View view) {
        startActivity(new Intent(this, SensorsActivity.class));
    }

    public void startScan(View view) {
        h.post(scan);
        h.post(orientation);
        startBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);
    }

    public void stopScan(View view) {
        h.removeCallbacks(scan);
        h.removeCallbacks(orientation);

        path.close();
        canvas.drawPath(path, paint);

        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);

        startActivity(new Intent(this, WifiListActivity.class));
    }
}
