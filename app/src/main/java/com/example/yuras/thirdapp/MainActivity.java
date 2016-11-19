package com.example.yuras.thirdapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private static final int delay = 5000;

    WifiManager mainWifi;
    WifiReceiver wifiScanReceiver;
    SensorsHandler sensorsHandler;

    Handler h;
    Runnable scan;
    Runnable orientation;
    ArrayList<String> list;

    Button startBtn;
    Button stopBtn;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        h = new Handler();

        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        sensorsHandler = new SensorsHandler(this);

        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        lv = (ListView) findViewById(R.id.lv);

        list = new ArrayList<>();

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
                h.postDelayed(scan, delay);
            }
        };

        orientation = new Runnable() {
            @Override
            public void run() {
                list.add(String.format(Locale.ENGLISH, "%.2f", sensorsHandler.getAzimuth()));

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, list);

                lv.setAdapter(adapter);

                h.postDelayed(orientation, 1000);
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
                List<String> wifi_names = new ArrayList<>();

                for (int i = 0; i < scanResults.size(); i++) {
                    wifi_names.add(scanResults.get(i).SSID);
                }

                Toast.makeText(getApplicationContext(), "Received",
                        Toast.LENGTH_SHORT).show();
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
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);

        Intent intent = new Intent(this, TrajectoryActivity.class);
        intent.putStringArrayListExtra("directions", list);
        startActivity(intent);
    }
}
