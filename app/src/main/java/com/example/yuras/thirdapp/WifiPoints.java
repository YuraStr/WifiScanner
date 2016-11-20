package com.example.yuras.thirdapp;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;

/**
 * Created by yuras on 20.11.2016.
 */

public class WifiPoints {
    private static ArrayList<WifiPointInfo> points = new ArrayList<>();
    private static ArrayList<String> ssids = new ArrayList<>();

    public static void add(WifiPointInfo point) {
        points.add(point);
        addPointSSIDs(point);
    }

    private static void addPointSSIDs(WifiPointInfo point) {
        if (ssids.size() == 0) {
            initSSIDs(point);
        } else {
            fillSSIDs(point);
        }
    }

    private static void initSSIDs(WifiPointInfo point) {
        for (ScanResult result : point.getScanResults()) {
            ssids.add(result.SSID);
        }
    }

    private static void fillSSIDs(WifiPointInfo point) {
        boolean fl = false;
        for(int i = 0; i < point.getScanResults().size(); i++) {
            String ssid = point.getScanResults().get(i).SSID;
            for (int j = 0; j < ssids.size(); j++) {
                if (ssid.equals(ssids.get(j))) {
                    fl = true;
                    break;
                }
            }
            if (!fl) {
                ssids.add(ssid);
            }
        }

    }

    public static ArrayList<Double> getMaxWifiLevelCoords(String ssid) {
        int maxLevel = -1;
        int maxPosition = -1;
        ArrayList<Double> coords = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            ScanResult point = points.get(i).findBySSID(ssid);
            if (point != null) {
                if (WifiManager.calculateSignalLevel(point.level, 10) > maxLevel) {
                    maxLevel = WifiManager.calculateSignalLevel(point.level, 10);
                    maxPosition = i;
                }
            }
        }

        coords.add(points.get(maxPosition).getX());
        coords.add(points.get(maxPosition).getY());
        return coords;
    }

    public static ArrayList<Double> getMinWifiLevelCoords(String ssid) {
        int minLevel = 11;
        int minPosition = 11;
        ArrayList<Double> coords = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            ScanResult point = points.get(i).findBySSID(ssid);
            if (point != null) {
                if (WifiManager.calculateSignalLevel(point.level, 10) < minLevel) {
                    minLevel = WifiManager.calculateSignalLevel(point.level, 10);
                    minPosition = i;
                }
            }
        }

        coords.add(points.get(minPosition).getX());
        coords.add(points.get(minPosition).getY());
        return coords;
    }

    public static ArrayList<String> getSSIDlist() {
        return ssids;
    }
}
