package com.coofee.wrapper.gson;

public interface Logger {

    Logger DUMP_EMPTY = new Logger() {

        @Override
        public boolean enableLog() {
            return false;
        }

        @Override
        public void d(String tag, String msg) {

        }

        @Override
        public void e(String tag, String msg) {

        }

        @Override
        public void e(String tag, String msg, Throwable e) {

        }
    };

    boolean enableLog();

    void d(String tag, String msg);

    void e(String tag, String msg);

    void e(String tag, String msg, Throwable e);
}
