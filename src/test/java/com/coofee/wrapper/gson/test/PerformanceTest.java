package com.coofee.wrapper.gson.test;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.google.gson.reflect.TypeToken;
import com.coofee.wrapper.gson.GsonWrapper;
import com.coofee.wrapper.gson.test.bean.Data;
import com.coofee.wrapper.gson.test.bean.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PerformanceTest {

    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule();

    @Test
    @JUnitPerfTest(warmUpMs = 5_000)
    public void test_decode() {
        final String originJson = GsonWrapper.toJson(new Response<Data>());
        Response<Data> decode = GsonWrapper.fromJson(originJson, new TypeToken<Response<Data>>() {
        }.getType());
    }
}
