package hcmute.edu.vn.nhom6.zalo.models;

import java.io.Serializable;

public class User implements Serializable {
    private String name, phoneNumber, token, lastMsgTime, lastMessage;
    private String encodedImg;

    public User(){};
    public User(String name, String encodedImg, String lastMessage, String lastMsgTime) {
        this.name = name;
        this.encodedImg = encodedImg;
        this.lastMessage = lastMessage;
        this.lastMsgTime = lastMsgTime;
    }

    public User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public User(String name, String phoneNumber, String encodedImg) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.encodedImg = encodedImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return encodedImg;
    }

    public void setImage(String encodedImg) {
        this.encodedImg = encodedImg;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
