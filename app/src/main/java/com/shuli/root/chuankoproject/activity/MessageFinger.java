package com.shuli.root.chuankoproject.activity;

import java.io.Serializable;

public class MessageFinger {
    private String name;
    private byte[] regTemplateData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getRegTemplateData() {
        return regTemplateData;
    }

    public void setRegTemplateData(byte[] regTemplateData) {
        this.regTemplateData = regTemplateData;
    }
}
