package com.coofee.wrapper.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Primitives;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;

public class GsonWrapper {
    static final String TAG = "GsonWrapper";

    static Logger sLogger = Logger.DUMP_EMPTY;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new FaultToleranceAdapterFactory())
            .create();

    public static void setLogImpl(Logger logImpl) {
        if (logImpl != null) {
            sLogger = logImpl;
        }
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static String toJson(Object object, Type type) {
        return GSON.toJson(object, type);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null || classOfT == null) {
            return null;
        }

        final Object object = fromJson(new StringReader(json), classOfT);

        try {
            return Primitives.wrap(classOfT).cast(object);
        } catch (Throwable e) {
            sLogger.e(TAG, "fail fromJson cast", e);
        }

        return null;
    }

    public static <T> T fromJson(String json, Type typeOf) {
        if (json == null || typeOf == null) {
            return null;
        }

        return fromJson(new StringReader(json), typeOf);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) {
        if (json == null || typeOfT == null) {
            return null;
        }

        try {
            JsonReader jsonReader = new JsonReader(json);
            return GSON.fromJson(jsonReader, typeOfT);
        } catch (Throwable e) {
            sLogger.e(TAG, "fail fromJson reader", e);
        }

        return null;
    }

    public static Gson gson() {
        return GSON;
    }
}
