package com.example.yuras.thirdapp;

import java.util.ArrayList;

/**
 * Created by yuras on 20.11.2016.
 */

public class Trajectory {
    private static ArrayList<Double> X = new ArrayList<>();
    private static ArrayList<Double> Y = new ArrayList<>();

    public static void addCoords(double x, double y) {
        X.add(x);
        Y.add(y);
    }

    public static ArrayList<Double> getCoordsX() {
        return X;
    }

    public static ArrayList<Double> getCoordsY() {
        return Y;
    }
}
