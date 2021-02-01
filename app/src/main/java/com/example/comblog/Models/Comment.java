package com.example.comblog.Models;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String content, uId, uImg, uName;
    private Object timeStamp;

    public Comment() {
    }

    public Comment(String content, String uId, String uImg, String uName) {
        this.content = content;
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuImg() {
        return uImg;
    }

    public void setuImg(String uImg) {
        this.uImg = uImg;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
