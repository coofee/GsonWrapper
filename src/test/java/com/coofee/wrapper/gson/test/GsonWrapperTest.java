package com.coofee.wrapper.gson.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coofee.wrapper.gson.GsonWrapper;
import com.coofee.wrapper.gson.Logger;
import com.coofee.wrapper.gson.test.bean.Data;
import com.coofee.wrapper.gson.test.bean.Data2;
import com.coofee.wrapper.gson.test.bean.Response;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntFunction;

@RunWith(JUnit4.class)
public class GsonWrapperTest {

    private static final String TAG = "GsonWrapperTest";

    private static final Logger sLogger = new SystemLogger();

    private static final Type sResponseDataType = new TypeToken<Response<Data>>() {
    }.getType();

    @BeforeClass
    public static void beforeClass() {
        GsonWrapper.setLogImpl(sLogger);
    }

    @Test
    public void test_hashMap_tableSizeFor() {
        final int MAXIMUM_CAPACITY = 1 << 30;

        IntFunction<Integer> tableSizeFor = (int cap) -> {

            int n = cap - 1;
            n |= n >>> 1;
            n |= n >>> 2;
            n |= n >>> 4;
            n |= n >>> 8;
            n |= n >>> 16;
            return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
        };

        sLogger.d(TAG, "test_hashMap_tableSizeFor; tableSizeFor(17)=" + tableSizeFor.apply(17));
    }

    @Test
    public void testJustForDebug() {
        final String originJson = GsonWrapper.toJson(new Response<Data>());
        sLogger.d(TAG, "testJustForDebug; originJson=" + originJson);

        final Response<Data> decode = GsonWrapper.fromJson(originJson, sResponseDataType);

        sLogger.d(TAG, "testJustForDebug; decode=" + decode);
    }

    @Test
    public void testNullJson() {
        final Gson gson = new Gson();
        final String originNullJson = gson.toJson(null);
        sLogger.d(TAG, "testNullJson; toJson, originNullJson=" + originNullJson);
        final String originNullFromJson = gson.fromJson(originNullJson, String.class);
        sLogger.d(TAG, "testNullJson; fromJson, originNullFromJson=" + originNullFromJson);

        Assert.assertEquals("testNullJson; toJson must equals",
                gson.toJson(null),
                GsonWrapper.toJson(null)
        );

        Assert.assertEquals("testNullJson; fromJson must equals",
                gson.fromJson(gson.toJson(null), String.class),
                GsonWrapper.fromJson(GsonWrapper.toJson(null), String.class)
        );

        sLogger.d(TAG, "testNullJson; toJson and fromJson equals. ");
    }

    @Test
    public void testNullField() {
        final String originJson = GsonWrapper.toJson(new Response<Data>());
        sLogger.d(TAG, "testNullField; originJson=" + originJson);

        final Response<Data> decode = GsonWrapper.fromJson(originJson, sResponseDataType);

        sLogger.d(TAG, "testNullField; decode=" + decode);

        Assert.assertNotNull("testNullField; decode must be not null", decode);

        Assert.assertNotNull("testNullField; decode.code must be not null", decode.code);
        Assert.assertEquals("testNullField; decode.code must be equals to default value", -1, decode.code);
        Assert.assertEquals("testNullField; decode.refCode must be equals to default value", (Integer) 3, decode.refCode);

        Assert.assertNotNull("testNullField; decode.msg must be not null", decode.msg);

        Assert.assertNotNull("testNullField; decode.stringMap must be not null", decode.stringMap);
        Assert.assertNotNull("testNullField; decode.pairList must be not null", decode.pairList);

        Assert.assertNotNull("testNullField; decode.data must be not null", decode.data);
        Assert.assertNotNull("testNullField; decode.data._id must be not null", decode.data._id);
        Assert.assertNotNull("testNullField; decode.data.points must be not null", decode.data.points);
        Assert.assertNotNull("testNullField; decode.data.strings must be not null", decode.data.strings);
    }

    @Test
    public void testArrayContainsNull() {
        final Response<Data> originResp = new Response<>();
        originResp.data = new Data();
        originResp.data.strings = new String[]{"a", null, "b", "c"};
        originResp.data.points = new Data.Point[]{null, null, new Data.Point()};

        final String originJson = GsonWrapper.toJson(originResp);
        sLogger.d(TAG, "testArrayContainsNull; originJson=" + originJson);

        Response<Data> decode = GsonWrapper.fromJson(originJson, sResponseDataType);
        sLogger.d(TAG, "testArrayContainsNull; decode=" + decode);

        for (String s : decode.data.strings) {
            Assert.assertNotNull("testArrayContainsNull; decode.data.strings must not contains null", s);
        }

        for (Data.Point point : decode.data.points) {
            Assert.assertNotNull("testArrayContainsNull; decode.data.points must not contains null", point);
        }
    }

    @Test
    public void testListContainsNull() {
        final Response<Data> originResp = new Response<>();
        originResp.pairList = new ArrayList<>();
        originResp.pairList.add(new Pair<>("1", 1));
        originResp.pairList.add(new Pair<>("2", 2));
        originResp.pairList.add(null);
        originResp.pairList.add(new Pair<>("3", 3));
        originResp.pairList.add(new Pair<>("4", 4));

        originResp.data = new Data();
        originResp.data.pointList = new ArrayList<>();
        originResp.data.pointList.add(new Data.Point());

        final String originJson = GsonWrapper.toJson(originResp);
        sLogger.d(TAG, "testListContainsNull; originJson=" + originJson);
        Response<Data> decode = GsonWrapper.fromJson(originJson, sResponseDataType);
        sLogger.d(TAG, "testListContainsNull; decode=" + decode);

        Assert.assertSame("testListContainsNull; must not contains null", false, decode.pairList.contains(null));
    }

    @Test
    public void testMapContainsNull() {
        final Response<Data> originResp = new Response<>();
        originResp.stringMap = new HashMap<>();
        originResp.stringMap.put("aaa", "1");
        originResp.stringMap.put(null, "nullKey");
        originResp.stringMap.put("bbb", "2");
        originResp.stringMap.put("nullValue", null);
        originResp.stringMap.put("ccc", "3");

        final String keyPoint1 = "point1";
        originResp.pointMap = new HashMap<>();
        originResp.pointMap.put(keyPoint1, new Data.Point());
        originResp.pointMap.put("point2_null", null);

        final String originJson = GsonWrapper.toJson(originResp);
        sLogger.d(TAG, "testMapContainsNull; originJson=" + originJson);
        Response<Data> decode = GsonWrapper.fromJson(originJson, sResponseDataType);
        sLogger.d(TAG, "testMapContainsNull; decode=" + decode);

        Assert.assertSame("testMapContainsNull; must not contains null key", false, decode.stringMap.containsKey(null));
        Assert.assertSame("testMapContainsNull; must not contains null value", false, decode.stringMap.containsValue(null));

        final Data.Point point = decode.pointMap.get(keyPoint1);
        Assert.assertSame("testMapContainsNull; must not contains null key", false, decode.pointMap.containsKey(null));
        Assert.assertSame("testMapContainsNull; must not contains null value", false, decode.pointMap.containsValue(null));
        Assert.assertNotNull("testMapContainsNull; point.extra must be not null", point.extra);
        Assert.assertNotNull("testMapContainsNull; point.extra must be not null", point.locations);
    }

    @Test
    public void testClassInherit() {
        final String dataJson = GsonWrapper.toJson(new Response<Data>());
        final Response<Data> dataResponse = GsonWrapper.fromJson(dataJson, sResponseDataType);
        sLogger.d(TAG, "testClassInherit; data=" + dataResponse);
        Assert.assertNotNull("testClassInherit; baseName must be not null", dataResponse.data.getBaseName());
    }

    @Test
    public void testFieldInfoCache() {
        final String dataJson = GsonWrapper.toJson(new Response<Data>());
        final Response<Data> data = GsonWrapper.fromJson(dataJson, sResponseDataType);
        sLogger.d(TAG, "testFieldInfoCache; data=" + data);
        Assert.assertNotNull("testFieldInfoCache; data must be not null", data.data);
        Assert.assertSame("testFieldInfoCache; data must be Data", true, (data.data instanceof Data));

        final String data2Json = GsonWrapper.toJson(new Response<Data2>());
        final Response<Data2> data2 = GsonWrapper.fromJson(data2Json, new TypeToken<Response<Data2>>() {
        }.getType());
        sLogger.d(TAG, "testFieldInfoCache; data2=" + data2);
        Assert.assertNotNull("testFieldInfoCache; data2 must be not null", data2.data);
        Assert.assertSame("testFieldInfoCache; data must be Data2", true, (data2.data instanceof Data2));
    }

    @Test
    public void testIntFormatError() {
        String invalidIntJson = "{\"code\":\"83993ks\", \"refCode\":\"903jsks90\",\"msg\":\"请求成功\",\"data\":{\"strings\":['a', null,'b'],\"points\":[],\"id\":\"9202\",\"name\":\"default_name\",\"desc\":\"\",\"pointList\":[]},\"stringMap\":{},\"pairList\":[]}\n";
        Response<Data> decode = GsonWrapper.fromJson(invalidIntJson, sResponseDataType);

        sLogger.d(TAG, "testParseIntFail; " + decode);

        // 1. 注意: 若是引用类型，当转换失败时，会设置为 注解中定义的默认值。
        Assert.assertEquals("testParseIntFail; testForRef must be -2", (Integer) (-2), decode.testForRef);

        // 2. 注意: 若是基本类型，当转换失败时，会设置为 {DefaultValues}中定义的默认值。
        Assert.assertEquals("testParseIntFail; code must be 0", -1, decode.code);

        // 出现1，2这种不一致是为了避免，当json中有字段包含了基本类型的默认值时(如: "{\"code\": 0}")，
        // 被注解定义的默认值替换掉。因为我们无法区分，code=0到底是json字符串中解析出来的，还是创建对象时的初始值。
        String jsonHasPrimitivesDefaultValue = GsonWrapper.toJson(new Response<Data>());
        sLogger.d(TAG, "testParseIntFail; jsonHasPrimitivesDefaultValue=" + jsonHasPrimitivesDefaultValue);

        decode = GsonWrapper.fromJson(jsonHasPrimitivesDefaultValue, sResponseDataType);
        Assert.assertEquals("testParseIntFail; code must be 0", -1, decode.code);

        String emptyJson = "{}";
        decode = GsonWrapper.fromJson(emptyJson, sResponseDataType);
        sLogger.d(TAG, "testParseIntFail; decode emptyJson=" + decode);
    }

    @Test
    public void testDataFormatError() {
        String dataListJson = "{\"code\":\"83993ks\", \"refCode\":\"903jsks90\",\"msg\":\"请求成功\",\"data\":[{\"strings\":['a', null,'b'],\"points\":[],\"id\":\"9202\",\"name\":\"default_name\",\"desc\":\"\",\"pointList\":[]}],\"stringMap\":{},\"pairList\":[]}";
        Response<Data> decode = GsonWrapper.fromJson(dataListJson, sResponseDataType);
        sLogger.d(TAG, "testDataFormatError; decode=" + decode);
        Assert.assertNotNull("testDataFormatError; decode.data must be not null", decode.data);

        String dataListNotEndJson = "{\"code\":\"83993ks\", \"refCode\":\"903jsks90\",\"msg\":\"请求成功\",\"data\":[{\"strings\":['a', null,'b'],\"points\":[],\"id\":\"9202\",\"name\":\"default_name\",\"desc\":\"\",\"pointList\":[]},\"stringMap\":{},\"pairList\":[]}";
        decode = GsonWrapper.fromJson(dataListNotEndJson, sResponseDataType);
        sLogger.d(TAG, "testDataFormatError; decode=" + decode);
        Assert.assertNull("testDataFormatError; decode must be null", decode);
    }
}
