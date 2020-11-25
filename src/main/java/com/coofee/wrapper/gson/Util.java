package com.coofee.wrapper.gson;

import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class Util {
    private static final String TAG = GsonWrapper.TAG + ".Util";

    static boolean excludeField(Excluder excluder, Field f) {
        return (excluder.excludeClass(f.getType(), false) || excluder.excludeField(f, false));
    }

    static boolean isArray(TypeToken typeToken) {
        Type type = typeToken.getType();
        return (type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray());
    }

    static Object getEmptyArray(TypeToken typeToken) {
        final Type arrayFieldType = typeToken.getType();
        final Type arrayComponentType = $Gson$Types.getArrayComponentType(arrayFieldType);
        final Class<?> arrayRawType = $Gson$Types.getRawType(arrayComponentType);

        Object emptyArray = DefaultValues.getEmptyArray(arrayRawType);
        if (emptyArray == null) {
            emptyArray = Array.newInstance(arrayRawType, 0);
        }

        return emptyArray;
    }

    static void removeNullFromCollection(Collection collection) {
        if (collection.isEmpty()) {
            return;
        }

        // remove null from Collection;
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (null == iterator.next()) {
                iterator.remove();
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.d(TAG, "removeNullFromCollection.");
                }
            }
        }
    }

    static void removeNullFromMap(Map map) {
        if (map.isEmpty()) {
            return;
        }

        // remove null key or value in map;
        map.remove(null);

        final Set<Map.Entry<?, ?>> entrySet = map.entrySet();
        Iterator<? extends Map.Entry> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            final Map.Entry next = iterator.next();
            if (next.getKey() == null || next.getValue() == null || "null".equals(next.getKey())) {
                iterator.remove();
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.d(TAG, "removeNullFromMap.");
                }
            }
        }
    }

    static Object removeNullFromArray(Object array, TypeToken typeToken) {
        final int originLength = Array.getLength(array);
        if (originLength <= 0) {
            return array;
        }

        int nullCount = 0;
        for (int i = 0; i < originLength; i++) {
            if (null == Array.get(array, i)) {
                nullCount++;
            }
        }

        if (nullCount > 0) {
            if (GsonWrapper.sLogger.enableLog()) {
                GsonWrapper.sLogger.d(TAG, "removeNullFromArray.");
            }

            final Type arrayFieldType = typeToken.getType();
            final Type arrayComponentType = $Gson$Types.getArrayComponentType(arrayFieldType);
            final Class<?> arrayRawType = $Gson$Types.getRawType(arrayComponentType);

            final Object newArray = Array.newInstance(arrayRawType, originLength - nullCount);
            for (int originIndex = 0, newArrayIndex = 0; originIndex < originLength; originIndex++) {
                Object element = Array.get(array, originIndex);
                if (null != element) {
                    Array.set(newArray, newArrayIndex++, element);
                }
            }

            return newArray;
        }

        return array;
    }
}
