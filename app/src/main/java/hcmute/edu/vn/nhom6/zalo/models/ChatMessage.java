package hcmute.edu.vn.nhom6.zalo.models;

import java.util.Date;

public class ChatMessage {
    private String senderId, receiverId, message, time, receiverImg, type;
    private Date dateObject;
    private String conversionId, conversionName, conversionImg, lastSenderId;
    private boolean isStoredSender, isStoredReceiver;

    public ChatMessage(String senderId, String receiverId, String message, String time, String type, Date dateObject, boolean isStoredSender, boolean isStoredReceiver) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.time = time;
        this.type = type;
        this.dateObject = dateObject;
        this.isStoredSender = isStoredSender;
        this.isStoredReceiver = isStoredReceiver;
    }

    public ChatMessage(){};

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDateObject() {
        return dateObject;
    }
    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getReceiverImg() {
        return receiverImg;
    }

    public void setReceiverImg(String receiverImg) {
        this.receiverImg = receiverImg;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getConversionName() {
        return conversionName;
    }

    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public String getConversionImg() {
        return conversionImg;
    }

    public void setConversionImg(String conversionImg) {
        this.conversionImg = conversionImg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastSenderId() {
        return lastSenderId;
    }

    public void setLastSenderId(String lastSenderId) {
        this.lastSenderId = lastSenderId;
    }

    public boolean isStoredSender() {
        return isStoredSender;
    }

    public void setStoredSender(boolean storedSender) {
        isStoredSender = storedSender;
    }

    public boolean isStoredReceiver() {
        return isStoredReceiver;
    }

    public void setStoredReceiver(boolean storedReceiver) {
        isStoredReceiver = storedReceiver;
    }
}
