package com.example.yuras.thirdapp;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by yuras on 20.11.2016.
 */

public class WifiPointInfo {
    private Double x;
    private Double y;
    private List<ScanResult> scanResults;

    public WifiPointInfo(List<ScanResult> scanResults, double x, double y) {
        this.x = x;
        this.y = y;
        this.scanResults = scanResults;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public ScanResult findBySSID(String ssid) {
        for (ScanResult result : scanResults) {
            if (result.SSID.equals(ssid)) {
                return result;
            }
        }
        return null;
    }
}
