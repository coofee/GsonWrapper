package com.coofee.wrapper.gson.test;

import com.coofee.wrapper.gson.Logger;

import java.io.PrintWriter;

public class SystemLogger implements Logger {

    @Override
    public boolean enableLog() {
        return true;
    }

    @Override
    public void d(String tag, String msg) {
        System.out.println("[" + tag + "] " + msg);
    }

    @Override
    public void e(String tag, String msg) {
        e(tag, msg, null);
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        if (e == null) {
            System.err.println("[" + tag + "] " + msg);
        } else {
            StringBuilder stringBuilder = new StringBuilder(1024);
            e.printStackTrace(new PrintWriter(new StringBuilderWriter(stringBuilder)));
            System.err.println("[" + tag + "] " + msg + stringBuilder.toString());
        }
    }
}
