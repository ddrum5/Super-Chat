package com.ddrum.superchatvippro.model;

public class Message {

    private String sender;
    private String receiver;
    private String text;
    private String type;
    private String time;
    private String seen;

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getPreTime() {
        return preTime; //???? e check hơn 3 p nó mói hiện thời gian
    }

    public void setPreTime(String preTime) {
        this.preTime = preTime;
    }

    private String preTime;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
