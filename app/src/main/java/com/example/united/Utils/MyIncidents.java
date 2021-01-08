package com.example.united.Utils;

public class MyIncidents {
    String Desc, ProtestImage, ProtestName;

    public MyIncidents() {
    }

    public MyIncidents(String desc, String protestImage, String protestName) {
        Desc = desc;
        ProtestImage = protestImage;
        ProtestName = protestName;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getProtestImage() {
        return ProtestImage;
    }

    public void setProtestImage(String protestImage) {
        ProtestImage = protestImage;
    }

    public String getProtestName() {
        return ProtestName;
    }

    public void setProtestName(String protestName) {
        ProtestName = protestName;
    }
}
