package hcmute.edu.vn.nhom6.zalo.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_AUDIO_PATH = "Zalo/Media/Recording"; // đường dẫn cho file ghi âm
    public static final String KEY_IMAGE_PATH = "Zalo/Media/Image"; // đường dẫn cho hình ảnh
    public static final String KEY_MEDIA_PATH = "Zalo/Media"; // đường dẫn cho media
    public static final String KEY_COLLECTION_USERS = "users"; // bảng users
    public static final String KEY_NAME = "name"; // thuộc tính name
    public static final String KEY_PHONE_NUMBER = "phoneNumber"; // thuộc tính sđt
    public static final String KEY_PASSWORD = "password"; // thuộc tính password
    public static final String KEY_PREFERENCE_NAME = "zaloClone";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId"; // thuộc tính user id
    public static final String KEY_IMAGE = "image"; // thuộc tính image
    public static final String KEY_FCM_TOKEN = "fcmToken"; // thuộc tính token
    public static final String KEY_ALL_CONTACT = "Tất cả";
    public static final String KEY_SYNC_DEVICE_CONTACT = "Bạn trong danh bạ";
    public static final String KEY_BEST_FRIEND = "Bạn thân";
    public static final String KEY_USER = "user";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_COLLECTION_CHAT = "chat"; // bảng chat
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations"; // bảng conversations
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
    public static final String MESSAGE_RECEIVED_A_PICTURE = " đã gửi cho bạn một ảnh";
    public static final String MESSAGE_SENT_A_AUDIO = "Bạn đã gửi tin nhắn thoại";
    public static final String MESSAGE_RECEIVED_A_AUDIO = " đã gửi cho bạn một tin nhắn thoại";
    public static final String MESSAGE_TEXT_AVAILABLE = "Đang hoạt động";
    public static final String MESSAGE_TEXT_NOT_AVAILABLE = "Không hoạt động";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String MESSAGE_NOTIFICATION_SUCCESS = "Thông báo tin nhắn thành công";
    public static final String MESSAGE_NO_CAMERA = "Không tìm thấy máy ảnh trên thiết bị này";
    public static final int KEY_CHANGE_PASSWORD_INTENT = 10; //code xác định intent cho verify otp -- intent change password
    public static final int KEY_SIGNUP_INTENT = 20; // code xác định intent cho verify otp -- intent signup
    public static final String KEY_INTENT_TO_VERIFY = "intentToVerify"; // intent cho verify otp
    public static final String KEY_LAST_SENDER_ID = "lastSenderId"; // người gửi tin nhắn mới nhất
    public static final String KEY_REMEMBER_PHONE = "phoneRemember"; // sdt
    public static final String KEY_REMEMBER_PASSWORD = "passwordRemember";  // ghi nhớ mật khẩu
    public static final String KEY_DELETE_PERIOD = "deletePeriod"; // khoảng thời gian tự động xóa
    public static final String KEY_IS_STORED_SENDER = "isStoredSender"; // giá trị cho biết file này người gửi cho xóa chưa -- khi true thì file sẽ được tải về máy người gửi (hoặc load lên nếu đã tải) ngược lại thì tức là file bị xóa trên máy người gửi và sẽ không tải lại trên máy người gửi (nhưng máy người nhận thì chưa xét)
    public static final String KEY_IS_STORED_RECEIVER = "isStoredReceiver"; // giá trị cho biết file này người nhận cho xóa chưa -- khi true thì file sẽ được tải về máy người nhận (hoặc load lên nếu đã tải) ngược lại thì tức là file bị xóa trên máy người nhận và sẽ không tải lại trên máy người nhận (nhưng máy người gửi thì chưa xét)

    public static HashMap<String, String> remoteMsgHeaders = null; // lưu thông tin của FCM

    /** Thiết lập thông tin header cho post request để gửi thông báo ở hàm sendMessage của APIService */
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
        return remoteMsgHeaders; // trả về header
    }
}
