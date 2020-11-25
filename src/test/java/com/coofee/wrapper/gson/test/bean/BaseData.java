package com.coofee.wrapper.gson.test.bean;


public class BaseData {

    private String baseName = "from_BaseData";

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public String toString() {
        return "BaseData{" +
                "baseName='" + baseName + '\'' +
                '}';
    }
}
