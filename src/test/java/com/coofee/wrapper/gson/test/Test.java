package com.coofee.wrapper.gson.test;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        System.out.println(Integer.MIN_VALUE);
        System.out.println(-Integer.MIN_VALUE);

        System.out.println(Integer.MIN_VALUE == (-Integer.MIN_VALUE));

        final Gson gson = new Gson();
        final InstallAppTask installAppTask = new InstallAppTask();
        installAppTask.appurl = "https://www.sss.xxx.com/xxx/index.html";
        String json = gson.toJson(installAppTask);
        System.out.println(json);

        json = "{\"needreboot\": 1,\"s\":[2,3,4,5],\"appurl\":[\"https://www.sss.xxx.com/xxx/index.html\", \"https://www.sss.xxx.com/xxx/index.html\", \"https://www.sss.xxx.com/xxx/index.html\"]}";
        final InstallAppTask installAppTask1 = gson.fromJson(json, InstallAppTask.class);
        System.out.println(installAppTask1);
    }

    @JsonAdapter(InstallAppTask.Adapter.class)
    public static class InstallAppTask {
        @SerializedName("appurl")
        public String appurl;

        @Override
        public String toString() {
            return "InstallAppTask{" +
                    "appurl='" + appurl + '\'' +
                    '}';
        }


        public static class Adapter extends TypeAdapter<InstallAppTask> {
            @Override
            public void write(JsonWriter out, InstallAppTask value) throws IOException {
                out.beginObject()
                        .name("appurl")
                        .beginArray()
                        .value(value.appurl)
                        .endArray()
                        .endObject();
            }

            @Override
            public InstallAppTask read(JsonReader in) throws IOException {
                JsonToken jsonToken = in.peek();
                if (JsonToken.BEGIN_OBJECT != jsonToken) {
                    return null;
                }

                InstallAppTask installAppTask = new InstallAppTask();
                in.beginObject();
                while (in.hasNext()) {
                    if (!"appurl".equals(in.nextName())) {
                        in.skipValue();
                        continue;
                    }

                    in.beginArray();
                    if (in.hasNext()) {
                        installAppTask.appurl = in.nextString();
                    }
                    while (in.hasNext()) {
                        in.skipValue();
                    }
                    in.endArray();
                }
                in.endObject();
                return installAppTask;
            }
        }

    }

}
