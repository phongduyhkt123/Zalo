package hcmute.edu.vn.nhom6.zalo.models;

import java.io.Serializable;

/** model người dùng */
public class User implements Serializable /* để có thể truyền vào intent */ {
    private String id, name, phoneNumber, token /* FCM token */;
    private String encodedImg; // ảnh đại diện encode base64

    public User(){};

    public User(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public User(String id, String name, String phoneNumber, String token, String encodedImg) {
        this(id, name, phoneNumber);
        this.encodedImg = encodedImg;
        this.token = token;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setEncodedImg(String encodedImg) {
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

    public String getId() {
        return id;
    }
}
