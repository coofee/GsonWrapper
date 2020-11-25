package com.coofee.wrapper.gson.test.bean;


import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class Response<T> {

    public int code = -1;

    public Integer testForRef = -2;

    public Integer refCode = 3;

    public String msg = "请求失败";

    public T data;

    public Map<String, String> stringMap;

    public List<Pair<String, Integer>> pairList;

    public Map<String, Data.Point> pointMap;

    private InnerClass innerClass;

    public class InnerClass {
        private String name;

        @Override
        public String toString() {
            return "InnerClass{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", testForRef=" + testForRef +
                ", refCode=" + refCode +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", stringMap=" + stringMap +
                ", pairList=" + pairList +
                ", pointMap=" + pointMap +
                ", innerClass=" + innerClass +
                '}';
    }
}
