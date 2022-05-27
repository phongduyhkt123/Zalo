package hcmute.edu.vn.nhom6.zalo.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_AUDIO_PATH = "Zalo/Media/Recording";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "zaloClone";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_ALL_CONTACT = "Tất cả";
    public static final String KEY_SYNC_DEVICE_CONTACT = "Bạn trong danh bạ";
    public static final String KEY_BEST_FRIEND = "Bạn thân";
    public static final String KEY_USER = "user";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String KEY_MESSAGE_TYPE = "messageType";
    public static final String KEY_PICTURE_MESSAGE = "picture";
    public static final String KEY_TEXT_MESSAGE = "text";
    public static final String KEY_AUDIO_MESSAGE = "audio";
    public static final String MESSAGE_SENT_A_PICTURE = "Bạn đã gửi một ảnh";
    public static final String MESSAGE_RECEIVED_A_PICTURE = "đã gửi cho bạn một ảnh";
    public static final String MESSAGE_SENT_A_AUDIO = "Bạn đã gửi tin nhắn thoại";
    public static final String MESSAGE_RECEIVED_A_AUDIO = "đã gửi cho bạn một tin nhắn thoại";
    public static final String MESSAGE_TEXT_AVAILABLE = "Đang hoạt động";
    public static final String MESSAGE_TEXT_NOT_AVAILABLE = "Không hoạt động";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String MESSAGE_NOTIFICATION_SUCCESS = "Thông báo tin nhắn thành công";
    public static final String MESSAGE_NO_CAMERA = "Không tìm thấy máy ảnh trên thiết bị này";
    public static final int KEY_CHANGE_PASSWORD_INTENT = 10;
    public static final int KEY_SIGNUP_INTENT = 20;
    public static final String KEY_INTENT_TO_VERIFY = "intentToVerify";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null){
             remoteMsgHeaders = new HashMap<>();
             remoteMsgHeaders.put(
                     REMOTE_MSG_AUTHORIZATION,
                     //copy trên firebase project setting - cloud message
                     "key=AAAATl4BdR4:APA91bGxyRliAiGx56CHnE21sKoU9WbObugYTH2zIBdKKWROjYnwxzKWAOnctbyXcBrPdSRaB_iWgpY310KNX1DOx7LiNWrHp0stNW2ThTSIyE5DlW1FCpyk_gmeOmVkEdYfm4zqoMDk"
             );
             remoteMsgHeaders.put(
                     REMOTE_CONTENT_TYPE,
                     "application/json"
             );
        }
        return remoteMsgHeaders;
    }
}
