package com.example.united.Utils;

public class PendingProtests {

    String Desc, ProtestImage, ProtestName, aganist, benifitTo, hashtag, status;

    public PendingProtests() {
    }

    public PendingProtests(String desc, String protestImage, String protestName, String aganist, String benifitTo, String hashtag, String status) {
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

    public void setBenfitTo(String benfitTo) {
        this.benifitTo = benfitTo;
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
