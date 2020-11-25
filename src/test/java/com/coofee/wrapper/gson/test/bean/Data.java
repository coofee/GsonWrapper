package com.coofee.wrapper.gson.test.bean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Data extends BaseData {
    public String[] strings;

    public Point[] points;

    public int _id;

    public Long id;

    private String name = "default_name";

    public String desc;

    public List<Point> pointList;

    public Long getId() {
        return id;
    }

    public static class Point {
        private float x;
        private float y;

        private String color;

        public Map<String, String> extra;

        public int[] locations;

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", color='" + color + '\'' +
                    ", extra=" + extra +
                    ", locations=" + Arrays.toString(locations) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Data{" +
                "strings=" + Arrays.toString(strings) +
                ", points=" + Arrays.toString(points) +
                ", _id=" + _id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", pointList=" + pointList +
                ", baseName='" + getBaseName() + '\'' +
                '}';
    }
}
