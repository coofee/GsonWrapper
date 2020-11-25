package com.coofee.wrapper.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class FaultToleranceTypeAdapter<T> extends TypeAdapter<T> {

    private static final String TAG = GsonWrapper.TAG + ".FaultTolerance";

    private static final ClassFieldInfoCache sClassFieldInfoCache = new ClassFieldInfoCache();

    private final Excluder mExcluder;

    private final TypeAdapter<T> mDelegateAdapter;

    private final TypeToken<T> mTypeToken;

    private final ConstructorConstructor mConstructorConstructor = new ConstructorConstructor(new HashMap<>());

    public FaultToleranceTypeAdapter(Gson gson, TypeToken<T> typeToken, TypeAdapter<T> delegate) {
        this.mExcluder = gson.excluder();
        this.mTypeToken = typeToken;
        this.mDelegateAdapter = delegate;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        mDelegateAdapter.write(out, value);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        // before decode;
        T value = null;
        boolean createByOur = false;

        try {
            value = mDelegateAdapter.read(in);
        } catch (Throwable e) {
            if (GsonWrapper.sLogger.enableLog()) {
                GsonWrapper.sLogger.e(TAG, "fail read type=" + mTypeToken, e);
            }

            createByOur = true;
            value = (T) createInstance();
            in.skipValue();
        }

        // after decode;
        if (value != null) {
            value = (T) fillObject(mTypeToken, value, createByOur);
        }

        return value;
    }

    private Object createInstance() {
        if (GsonWrapper.sLogger.enableLog()) {
            GsonWrapper.sLogger.d(TAG, "createInstance, typeToken=" + mTypeToken);
        }

        final Type type = mTypeToken.getType();
        if (DefaultValues.contains(type)) {
            /// will override when create object set field.
//            return DefaultValues.get(type);
            return null;
        }

        if (Util.isArray(mTypeToken)) {
            return Util.getEmptyArray(mTypeToken);
        }

        final ObjectConstructor tObjectConstructor = mConstructorConstructor.get(mTypeToken);
        return tObjectConstructor.construct();
    }

    private Object fillObject(TypeToken typeToken, Object value, boolean createByOur) {
        final Class<?> vClass = value.getClass();
        if (DefaultValues.contains(vClass)) {
            if (GsonWrapper.sLogger.enableLog()) {
                GsonWrapper.sLogger.d(TAG, "fillObject; skip DefaultValues.Type class=" + vClass + ", value=" + value + ", just return.");
            }
            return value;
        }

        if (value instanceof Collection) {
            Util.removeNullFromCollection((Collection) value);
            return value;
        }

        if (value instanceof Map) {
            Util.removeNullFromMap((Map) value);
            return value;
        }

        if (Util.isArray(typeToken)) {
            Object newArray = Util.removeNullFromArray(value, typeToken);
            if (value != newArray) {
                return newArray;
            }
            return value;
        }

        final HashSet<ClassFieldInfoCache.FieldInfo> fieldInfoSet = sClassFieldInfoCache.getFieldInfo(typeToken, mExcluder);
        if (fieldInfoSet == null) {
            return value;
        }

        for (ClassFieldInfoCache.FieldInfo fieldInfo : fieldInfoSet) {
            try {
                setField(fieldInfo, value, createByOur);
            } catch (Throwable e) {
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.e(TAG, "fillObject; fail setField; fieldName=" + fieldInfo.field.getName(), e);
                }
            }
        }

        return value;
    }

    private void setField(ClassFieldInfoCache.FieldInfo fieldInfo, Object object, boolean createByOur) throws IllegalAccessException {
        final Object currentValue = fieldInfo.field.get(object);
        if (currentValue != null) {
            if (GsonWrapper.sLogger.enableLog()) {
                GsonWrapper.sLogger.d(TAG, "setField; already parsed value from json, just return.");
            }
            return;
        }

        final String fieldName = fieldInfo.field.getName();
        switch (fieldInfo.type) {
            case ClassFieldInfoCache.FieldInfo.TYPE_PRIMITIVE_OR_STRING:
                fieldInfo.field.set(object, fieldInfo.defaultValue);
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.d(TAG, "setField; primitive or string type; set " + fieldName + "=" + fieldInfo.defaultValue);
                }
                break;

            case ClassFieldInfoCache.FieldInfo.TYPE_ARRAY:
                fieldInfo.field.set(object, fieldInfo.defaultValue);
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.d(TAG, "setField; reference type array; set " + fieldName + "=" + fieldInfo.defaultValue);
                }
                break;

            case ClassFieldInfoCache.FieldInfo.TYPE_OBJECT:
                final ObjectConstructor tObjectConstructor = mConstructorConstructor.get(fieldInfo.typeToken);
                final Object fieldObject = tObjectConstructor.construct();
                fieldInfo.field.set(object, fieldObject);

                fillObject(fieldInfo.typeToken, fieldObject, true);
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.d(TAG, "setField; reference type object; set " + fieldName + "=" + fieldObject + ", fieldObject.type=" + fieldObject.getClass());
                }
                break;

            default:
                // ignore; unreachable
                if (GsonWrapper.sLogger.enableLog()) {
                    GsonWrapper.sLogger.e(TAG, "setField; unreachable fieldInfo=" + fieldInfo);
                }
                break;
        }
    }
}
