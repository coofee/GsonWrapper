package com.coofee.wrapper.gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultValues {
    private static final Map<Class<?>, Object> DEFAULT_VALUES;

    private static final Map<Class<?>, Object> EMPTY_ARRAYS;

    static {
        Map<Class<?>, Object> defaultValues = new HashMap<>();

        defaultValues.put(Boolean.TYPE, false);
        defaultValues.put(Boolean.class, false);

        defaultValues.put(Character.TYPE, (char) 0);
        defaultValues.put(Character.class, (char) 0);

        defaultValues.put(Byte.TYPE, (byte) 0);
        defaultValues.put(Byte.class, (byte) 0);

        defaultValues.put(Short.TYPE, (short) 0);
        defaultValues.put(Short.class, (short) 0);

        defaultValues.put(Integer.TYPE, 0);
        defaultValues.put(Integer.class, 0);

        defaultValues.put(Long.TYPE, 0L);
        defaultValues.put(Long.class, 0L);

        defaultValues.put(Float.TYPE, (float) 0);
        defaultValues.put(Float.class, (float) 0);

        defaultValues.put(Double.TYPE, (double) 0);
        defaultValues.put(Double.class, (double) 0);

        defaultValues.put(String.class, "");
        DEFAULT_VALUES = Collections.unmodifiableMap(defaultValues);


        Map<Class<?>, Object> emptyArrays = new HashMap<>();

        emptyArrays.put(Boolean.TYPE, new boolean[0]);
        emptyArrays.put(Boolean.class, new Boolean[0]);

        emptyArrays.put(Character.TYPE, new char[0]);
        emptyArrays.put(Character.class, new Character[0]);

        emptyArrays.put(Byte.TYPE, new byte[0]);
        emptyArrays.put(Byte.class, new Byte[0]);

        emptyArrays.put(Short.TYPE, new short[0]);
        emptyArrays.put(Short.class, new Short[0]);

        emptyArrays.put(Integer.TYPE, new int[0]);
        emptyArrays.put(Integer.class, new Integer[0]);

        emptyArrays.put(Long.TYPE, new long[0]);
        emptyArrays.put(Long.class, new Long[0]);

        emptyArrays.put(Float.TYPE, new float[0]);
        emptyArrays.put(Float.class, new Float[0]);

        emptyArrays.put(Double.TYPE, new double[0]);
        emptyArrays.put(Double.class, new Double[0]);

        emptyArrays.put(String.class, new String[0]);
        EMPTY_ARRAYS = Collections.unmodifiableMap(emptyArrays);
    }

    public static <T> T get(Class<T> classOf) {
        return (T) DEFAULT_VALUES.get(classOf);
    }

    static <T> T get(Type type) {
        return (T) DEFAULT_VALUES.get(type);
    }

    public static boolean contains(Class<?> classOf) {
        return DEFAULT_VALUES.containsKey(classOf);
    }

    public static boolean contains(Type type) {
        return DEFAULT_VALUES.containsKey(type);
    }

    public static <T> T getEmptyArray(Class<?> classOf) {
        return (T) EMPTY_ARRAYS.get(classOf);
    }
}
