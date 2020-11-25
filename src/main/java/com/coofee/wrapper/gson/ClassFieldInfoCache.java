package com.coofee.wrapper.gson;

import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class ClassFieldInfoCache {

    private static final String TAG = GsonWrapper.TAG + ".ClassFieldInfoCache";

    private final Object mLock = new Object();
    private final Map<TypeToken<?>, HashSet<FieldInfo>> mFieldInfoCache = new HashMap<>();

    HashSet<FieldInfo> getFieldInfo(TypeToken typeToken, Excluder excluder) {
        Class<?> rawType = typeToken.getRawType();
        if (rawType.isInterface()) {
            if (GsonWrapper.sLogger.enableLog()) {
                GsonWrapper.sLogger.e(TAG, "getFieldInfo; class is interface.");
            }

            return null;
        }

        HashSet<FieldInfo> fieldInfoSet = mFieldInfoCache.get(typeToken);
        if (fieldInfoSet != null) {
            return fieldInfoSet;
        }

        synchronized (mLock) {
            fieldInfoSet = mFieldInfoCache.get(typeToken);
            if (fieldInfoSet != null) {
                return fieldInfoSet;
            }

            final TypeToken<?> originTypeToken = typeToken;
            fieldInfoSet = new HashSet<>();
            Type type = typeToken.getType();

            while (rawType != Object.class) {
                getFieldInfo(type, rawType, excluder, fieldInfoSet);
                typeToken = TypeToken.get($Gson$Types.resolve(type, rawType, rawType.getGenericSuperclass()));
                type = typeToken.getType();
                rawType = typeToken.getRawType();
            }

            mFieldInfoCache.put(originTypeToken, fieldInfoSet);
        }

        if (GsonWrapper.sLogger.enableLog()) {
            GsonWrapper.sLogger.d(TAG, "getFieldInfo; mFieldInfoCache.size()=" + mFieldInfoCache.size() + ", " + mFieldInfoCache.keySet());
            GsonWrapper.sLogger.d(TAG, "getFieldInfo; fieldInfoSet.size()=" + fieldInfoSet.size() + ", " + fieldInfoSet);
        }

        return fieldInfoSet;
    }

    private static void getFieldInfo(Type type, Class<?> rawType, Excluder excluder, HashSet<FieldInfo> fieldInfoSet) {
        final Field[] declaredFields = rawType.getDeclaredFields();
        if (declaredFields == null || declaredFields.length <= 0) {
            return;
        }

        for (Field f : declaredFields) {
            if (Util.excludeField(excluder, f)) {
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.e(TAG, "getFieldInfo; skip exclude field; fieldName=" + f.getName() + " of " + f.getType());
                }

                continue;
            }

            fieldInfoSet.add(getFieldInfo(f, type, rawType));
        }
    }

    private static FieldInfo getFieldInfo(Field f, Type objectType, Class<?> objectRawType) {
        f.setAccessible(true);

        if (DefaultValues.contains(f.getType())) {
            return new FieldInfo(f, FieldInfo.TYPE_PRIMITIVE_OR_STRING, DefaultValues.get(f.getType()));
        }

        final TypeToken filedTypeToken = TypeToken.get($Gson$Types.resolve(objectType, objectRawType, f.getGenericType()));

        if (Util.isArray(filedTypeToken)) {
            return new FieldInfo(f, FieldInfo.TYPE_ARRAY, Util.getEmptyArray(filedTypeToken));
        }

        return new FieldInfo(f, FieldInfo.TYPE_OBJECT, filedTypeToken);
    }

    static class FieldInfo {
        static final int TYPE_PRIMITIVE_OR_STRING = 0;

        static final int TYPE_ARRAY = 1;

        static final int TYPE_OBJECT = 2;

        final Field field;

        /**
         * {@link #TYPE_PRIMITIVE_OR_STRING}, {@link #TYPE_ARRAY}, {@link #TYPE_OBJECT}
         */
        final int type;

        /***
         * only for {@link #TYPE_PRIMITIVE_OR_STRING} and {@link #TYPE_ARRAY}
         */
        final Object defaultValue;

        /**
         * only for {@link #TYPE_OBJECT}
         */
        final TypeToken<?> typeToken;

        private FieldInfo(Field field, int type, Object defaultValue) {
            this.field = field;
            this.defaultValue = defaultValue;
            this.type = type;

            this.typeToken = null;
        }

        private FieldInfo(Field field, int type, TypeToken<?> fieldTypeToken) {
            this.field = field;
            this.type = type;
            this.typeToken = fieldTypeToken;

            this.defaultValue = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            FieldInfo fieldInfo = (FieldInfo) o;

            if (type != fieldInfo.type) {
                return false;
            }
            if (field != null ? !field.equals(fieldInfo.field) : fieldInfo.field != null) {
                return false;
            }
            if (defaultValue != null ? !defaultValue.equals(fieldInfo.defaultValue) : fieldInfo.defaultValue != null) {
                return false;
            }
            return typeToken != null ? typeToken.equals(fieldInfo.typeToken) : fieldInfo.typeToken == null;
        }

        @Override
        public int hashCode() {
            int result = field != null ? field.hashCode() : 0;
            result = 31 * result + type;
            result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
            result = 31 * result + (typeToken != null ? typeToken.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "FieldInfo{" +
                    "name='" + field.getName() + "\'" +
                    ", field=" + field +
                    ", defaultValue=" + defaultValue +
                    ", type=" + type +
                    ", typeToken=" + typeToken +
                    '}';
        }
    }
}
