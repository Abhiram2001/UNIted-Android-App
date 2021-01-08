package com.example.united.Utils;

public class Incidents {

    String Desc, ProtestImage, ProtestName, aganist, benifitTo, hashtag, status;

    public Incidents() {
    }

    public Incidents(String desc, String protestImage, String protestName, String aganist, String benifitTo, String hashtag, String status) {
        Desc = desc;
        ProtestImage = protestImage;
        ProtestName = protestName;
        this.aganist = aganist;
        this.benifitTo = benifitTo;
        this.hashtag = hashtag;
        this.status = status;
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

    public String getAganist() {
        return aganist;
    }

    public void setAganist(String aganist) {
        this.aganist = aganist;
    }

    public String getBenifitTo() {
        return benifitTo;
    }

    public void setBenifitTo(String benifitTo) {
        this.benifitTo = benifitTo;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
