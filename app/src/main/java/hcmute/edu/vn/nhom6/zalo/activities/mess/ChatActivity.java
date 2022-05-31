package hcmute.edu.vn.nhom6.zalo.activities.mess;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.devlomi.record_view.OnRecordListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.activities.profile.FriendProfileActivity;
import hcmute.edu.vn.nhom6.zalo.adapters.ChatAdapter;
import hcmute.edu.vn.nhom6.zalo.databinding.DialogChooseSendPhotoMethodBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.FragmentOpenChatBinding;
import hcmute.edu.vn.nhom6.zalo.models.ChatMessage;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.network.APIClient;
import hcmute.edu.vn.nhom6.zalo.network.APIService;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyActivityForResult;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity /*with user availability*/ {

    private static final int REQUEST_CODE_AUDIO_RECORD = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;
    private MediaRecorder mRecorder;
    private boolean recordAllow = false;
    private FragmentOpenChatBinding binding;
    private PreferenceManager preferenceManager;
    private User uReceiver; // người nhận tin nhắn
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ArrayList<ChatMessage> chatList; // danh sách tin nhắn
    private String conversionId = null;
    private String messageType = Constants.KEY_TEXT_MESSAGE;
    private boolean isReceiverAvailable = false;
    private String audioPath;
    private String audioName;
    private boolean audioRecordResp = false;
    private boolean writeExStorageResp = false;

    protected final MyActivityForResult<Intent, ActivityResult> activityLauncher = MyActivityForResult.registerActivityForResult(this);
    // thay thế cho startActivityForResult
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        if(result.getData() != null){
                            Bitmap bitmap = null;
                            if(result.getData().getData() != null){ // Nếu chọn hình ảnh từ thư viện
                                Uri imageUri = result.getData().getData();
                                try{
                                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                    bitmap = BitmapFactory.decodeStream(inputStream);
                                }catch (FileNotFoundException e){
                                    e.printStackTrace();
                                }
                            }else{ // nếu chụp ảnh
                                bitmap = (Bitmap) result.getData().getExtras().get("data");
                            }
                            binding.layoutImgPreview.setVisibility(View.VISIBLE);
                            binding.imgPreview.setImageBitmap(bitmap);
                            messageType = Constants.KEY_PICTURE_MESSAGE;
                            binding.edittextChatMessage.setText(null);
                            binding.edittextChatMessage.setEnabled(false);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentOpenChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetail();
        init();
        listenMessage();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatList = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        chatAdapter = new ChatAdapter(
                chatList,
                MyUtilities.decodeImg(uReceiver.getImage()),
                preferenceManager.getString(Constants.KEY_USER_ID),
                storage.getReference()
        );
        binding.recyclerOpenChannelChat.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();

    }

    private void loadReceiverDetail(){
        uReceiver = (User) getIntent().getSerializableExtra(Constants.KEY_USER); // lấy đối tượng người nhận được truyền lúc nhấn vào trong danh bạ hoặc màn hình tin nhắn tổng
        binding.txtName.setText(uReceiver.getName());
    }


//    private void loading(Boolean isLoading) {
//        if(isLoading) {
//            binding.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            binding.progressBar.setVisibility(View.INVISIBLE);
//        }
//    }

    private void setListeners(){
        binding.backButton.setOnClickListener( v -> onBackPressed()); // sự kiện nhấn nút trở về trên android
        binding.buttonOpenChannelChatSend.setOnClickListener(v -> sendMessage());

        binding.buttonOpenChannelChatUpload.setOnClickListener( v -> openDialogSendPhoto());
        binding.buttonDeleteImg.setOnClickListener(v -> {
            binding.imgPreview.setImageBitmap(null);
            binding.layoutImgPreview.setVisibility(View.GONE);
            binding.edittextChatMessage.setEnabled(true); // cho phép gõ chữ
            messageType = Constants.KEY_TEXT_MESSAGE; // kiểu text
        });

        binding.imgVoiceMessage.setOnClickListener(v -> {
            onVoiceMessageClick();
        });

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if(startRecordAudio())
                    MyUtilities.showToast(getApplicationContext(), "Bắt đầu ghi âm");
                else
                    onCancel();
            }

            @Override
            public void onCancel() {
                MyUtilities.showToast(getApplicationContext(), "Đã hủy");
                stopRecord();

                //Xóa file ghi âm
                File file = new File(audioPath);
                if(file.exists())
                    file.delete();

                binding.layoutChat.setVisibility(View.VISIBLE);
                binding.layoutRecord.setVisibility(View.GONE);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                MyUtilities.showToast(getApplicationContext(), "Ghi âm hoàn tất");
                stopRecord();
            }

            @Override
            public void onLessThanSecond() {
                MyUtilities.showToast(getApplicationContext(), "Đoạn ghi âm quá ngắn!");
                mRecorder.reset();
                mRecorder.release();

                //Xóa file ghi âm
                File file = new File(audioPath);
                if(file.exists())
                    file.delete();
            }
        });

        binding.menuChat.setOnClickListener(v ->{
            showMenu(v);
        });

        binding.txtName.setOnClickListener( v -> {
            openProfile(uReceiver);
        });

    }

    private void openProfile(User user) {
        Intent intent = new Intent(getApplicationContext(), FriendProfileActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

    private void onVoiceMessageClick(){
        if(binding.layoutRecord.getVisibility() != View.VISIBLE){
            setUpRecordAudio();
            if(recordAllow){
                binding.layoutRecord.setVisibility(View.VISIBLE);
                binding.recordButton.setRecordView(binding.recordView);
                binding.edittextChatMessage.setEnabled(false);
                binding.buttonOpenChannelChatUpload.setEnabled(false);
            }
        }else{
            binding.layoutRecord.setVisibility(View.GONE);
            binding.edittextChatMessage.setText(null); // làm mới edittext nhập tin nhắn
            binding.edittextChatMessage.setEnabled(true); // cho phép gõ chữ
            binding.buttonOpenChannelChatUpload.setEnabled(true);
            messageType = Constants.KEY_TEXT_MESSAGE; // kiểu text
        }

    }

    /** chuẩn bị các quyền như AudioRecord, ExternalStorage để có thể ghi âm*/
    private void setUpRecordAudio(){
        // Kiểm tra xem ứng dụng có quyền truy cập vào audio record không
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){ // chưa có quyền
            Log.e("setUpRecordAudio", "no audio record permission!");
            requestAudioRecordPermission(); // yêu cầu quyền
            return;
        }
        /* nếu SKD version nhỏ hơn 30 thì cần write external storage permission
        * nhưng từ 30 trở lên thì cần manage external storage permission*/
        if(Build.VERSION.SDK_INT < 30){
            // Kiểm tra xem ứng dụng có quyền truy cập vào write_external_storage khong
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) { // chưa có quyền
                Log.e("setUpRecordAudio", "no write external storage permission!");
                requestWriteExternalStorage(); // yêu cầu quyền
                return;
            }
        }else{
            // Kiểm tra xem ứng dụng có quyền truy cập vào manage_external_storage khong
            if (!Environment.isExternalStorageManager()) { // chưa có quyền
                Log.e("setUpRecordAudio", "no manage external storage permission!");
                requestManageExternalStorage();// yêu cầu quyền
                return;
            }
        }

        /* xuống đến đây có nghĩa là các quyền truy cập đã thỏa mãn vậy có thể record được rồi*/
        recordAllow = true;
//        writeExStorageResp = true;
//        audioRecordResp = true;

        binding.layoutRecord.setVisibility(View.VISIBLE);
        binding.recordButton.setRecordView(binding.recordView);
    }
    /** yêu cầu quyền truy cập audio record */
    private void requestAudioRecordPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){ //
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_CODE_AUDIO_RECORD);
        }else{ //
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_CODE_AUDIO_RECORD);
        }
    }
    /** yêu cầu quyền truy cập write external storage */
    private void requestWriteExternalStorage() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){ //
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }else{ //
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }
    /** yêu cầu quyền truy cập manage external storage (cho API 30 trở lên)*/
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageExternalStorage() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        activityLauncher.launch(intent, result ->{
            if(Environment.isExternalStorageManager()){
                setUpRecordAudio();
            }else{
                MyUtilities.showToast(this, "permission not granted");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_AUDIO_RECORD){
//            audioRecordResp = true;
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                MyUtilities.showToast(this, "permission granted");
                setUpRecordAudio();
            }else{
                MyUtilities.showToast(this, "permission not granted");
            }
        }
        if(requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE){
//            writeExStorageResp = true;
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                MyUtilities.showToast(this, "permission granted");
                setUpRecordAudio();
            }else{
                MyUtilities.showToast(this, "permission not granted");
            }
        }
    }

    /** bắt đầu ghi âm*/
    private boolean startRecordAudio() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(getExternalCacheDir()+ "/"+ new Date().toString() + ".3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        audioName = MyUtilities.randomString() + ".3gp";
        audioPath = file.getAbsolutePath() + File.separator + audioName;
//        audioPath = file.getAbsolutePath()+File.separator+ new Date().toString() + ".3gp";
        mRecorder.setOutputFile(audioPath);
        Log.e("AUDIO FILE", audioPath);
        try {
            mRecorder.prepare();
            mRecorder.start();
            messageType = Constants.KEY_AUDIO_MESSAGE;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("startRecord", "Có lỗi khi chuẩn bị ghi âm");
            return false;
        }
        binding.layoutRecord.setVisibility(View.VISIBLE);
        return true;
    }

    /** kết thúc ghi âm và giải phóng tài nguyên */
    private boolean stopRecord(){
        try{
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            return true;
        }catch (IllegalStateException e){
            Log.e("stopRecord", "Audio không tồn tại");
        }catch (NullPointerException e){
            Log.e("stopRecord", "Audio không tồn tại");
        }
        return false;
    }

    /** Đưa audio lên firestore*/

    private void openChooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        mActivityResultLauncher.launch(intent);
    }

    /** thực hiện lấy lịch sử trò chuyện
     * lắng nghe sự thay đổi của csdl firebase ở bảng chat
     * với các dòng chat có sự tham gia của current user (sender hoặc receiver)
     * và receiver của chatActivity (sender hoặc receiver)
     * nếu có thay đổi thì cập nhật lại recycleView hiển thị lịch sử trò chuyện của 2 user này*/
    private void listenMessage(){
        // Lấy tin nhắn current user là người gửi
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, uReceiver.getId())
                .addSnapshotListener(eventListener);/*nếu có thay đổi thì gọi eventListener để cập nhật recycleView.
                                                                    truyền vào activity This để listener này chỉ lắng khi người dùng đang mở chatActivity*/

        // Lấy tin nhắn current user là người nhận
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, uReceiver.getId())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    /** thực hiện gửi thông báo tin nhắn mới đến người nhận khi người nhận không hoạt động */
    private void sendNotification(String msg){
        APIClient.getClient().create(APIService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                msg
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                MyUtilities.showToast(getApplicationContext(), error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MyUtilities.showToast(getApplicationContext(), Constants.MESSAGE_NOTIFICATION_SUCCESS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                MyUtilities.showToast(getApplicationContext(), t.getMessage());
            }
        });
    }

    /**  */
    private void listenAvailabilityOfReceiver(){
        db.collection(Constants.KEY_COLLECTION_USERS).document(uReceiver.getId())
                .addSnapshotListener(ChatActivity.this, (value, error) ->{
                    if(error != null){
                        error.printStackTrace();
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        /* lấy trạng thái hoạt động của người nhận */
                        if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                            int availability = Objects.requireNonNull(
                                    value.getLong(Constants.KEY_AVAILABILITY)
                            ).intValue();
                            isReceiverAvailable = availability == 1;
                        }
                        uReceiver.setToken(value.getString(Constants.KEY_FCM_TOKEN)); // lấy token của người nhận
                        if(uReceiver.getImage() == null){
                            uReceiver.setEncodedImg(value.getString(Constants.KEY_IMAGE));
                            chatAdapter.setReceiverProfileImg(MyUtilities.decodeImg(uReceiver.getImage()));
                            chatAdapter.notifyItemRangeChanged(0, chatList.size());
                        }
                    }
                    if(isReceiverAvailable){
                        binding.txtStatus.setText(Constants.MESSAGE_TEXT_AVAILABLE);
                        binding.imgStatus.setVisibility(View.VISIBLE);
                    }else {
                        binding.txtStatus.setText(Constants.MESSAGE_TEXT_NOT_AVAILABLE);
                        binding.imgStatus.setVisibility(View.GONE);
                    }
                });
    }

    /** Thực hiện cập nhật lịch sử cuộc trò chuyện hiện tại (chatList), (lấy lịch sử trò chuyện nếu mới mở cửa sổ)
     * Ngoài ra, còn kiểm tra xem 2 người dùng này trước đó đã có trò chuyện chưa (có chung conversation chưa)
     * Nếu chưa có conversation chung thì tạo conversation cho 2 người trong firebase*/
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chatList.size();
            /* Nếu có tin nhắn mới thì thêm tin nhắn vào chatList để cập nhật RecycleView */
            for(DocumentChange i: value.getDocumentChanges()){
                if(i.getType() == DocumentChange.Type.ADDED){ // Nếu có tin nhắn mới
                    ChatMessage message = new ChatMessage(
                            i.getDocument().getString(Constants.KEY_SENDER_ID),
                            i.getDocument().getString(Constants.KEY_RECEIVER_ID),
                            i.getDocument().getString(Constants.KEY_MESSAGE),
                            MyUtilities.getStringDate(i.getDocument().getDate(Constants.KEY_TIMESTAMP)),
                            i.getDocument().getString(Constants.KEY_MESSAGE_TYPE),
                            i.getDocument().getDate(Constants.KEY_TIMESTAMP)
                    );
                    chatList.add(message);
                }
            }
            // sắp xếp danh sách tin nhắn theo thời gian được gửi
            Collections.sort(chatList, (obj1, obj2) -> obj1.getDateObject().compareTo(obj2.getDateObject()));

            /*Để tiết kiệm tài nguyên thì nếu tập data của adapter chưa có gì thì ta mới gọi notifyDatasetChanged
            * ngược lại thì gọi notifyItemRangeInserted để chỉ thêm tin nhắn mới vào chứ không cần kiểm tra lại toàn bộ tin nhắn trong tập data của adapter*/

            if(count == 0) // nếu lúc đầu chưa có tin nhắn thì gọi notifyDataSetChanged để cập nhật tin nhắn
                chatAdapter.notifyDataSetChanged();
            else { // nếu có tin nhắn rồi thì gọi notifyItemRangeInserted để thêm tin nhắn mới vào (không cần cập nhật hết toàn bộ)
                chatAdapter.notifyItemRangeInserted(chatList.size(), chatList.size()); // vị trí, số lượng
                binding.recyclerOpenChannelChat.smoothScrollToPosition(chatList.size() - 1); // trượt đến vị trí tin nhắn mới
            }
        }
        if(conversionId == null){
            checkForConversion(); //lấy conversionId
        }
    };

    //Tạo conversion ở db
    private void addConversion(HashMap<String, Object> conversion){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId()); //cập nhật conversionId

    }
    // cập nhật conversion
    private void updateConversion(String message){
        // lấy conversion từ db với id là conversionId
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);

        // cập nhật conversion -- tin nhắn cuối cùng, thời gian, người gửi cuối cùng
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_MESSAGE_TYPE, messageType,
                Constants.KEY_TIMESTAMP, new Date(),
                Constants.KEY_LAST_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID)
        );
    }
    /** Gửi tin nhắn
     * Lưu tin nhắn lên firebase
     * Tạo conversation mới nếu chưa có conversation ứng với tin nhắn hiện tại
     * Cập nhật conversation nếu đã có conversation
     * Gửi thông báo đến người nhận*/
    private void sendMessage(){
        // thêm tin nhắn vào cơ sở dữ liệu
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, uReceiver.getId());
        String mes = "";
        if(messageType.equals(Constants.KEY_TEXT_MESSAGE)){
            mes = binding.edittextChatMessage.getText().toString();
        }else if(messageType.equals(Constants.KEY_PICTURE_MESSAGE)){
            mes = MyUtilities.encodeImg(((BitmapDrawable)binding.imgPreview.getDrawable()).getBitmap(), 300);
        }else{
            mes = audioName;
            sendAudioToFireBase();
        }
        message.put(Constants.KEY_MESSAGE, mes);
        message.put(Constants.KEY_MESSAGE_TYPE, messageType);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        db.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionId != null){ // nếu conversion đã tồn tại (được lấy lúc đầu ở listenMessage - eventListener)
                                // thì cập nhật conversion
            updateConversion(message.get(Constants.KEY_MESSAGE).toString());
        }else{ // ngược lại tạo conversion mới
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_RECEIVER_NAME, uReceiver.getName());
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_IMAGE, uReceiver.getImage());
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_RECEIVER_ID, uReceiver.getId());
            conversion.put(Constants.KEY_LAST_MESSAGE, message.get(Constants.KEY_MESSAGE).toString());
            conversion.put(Constants.KEY_MESSAGE_TYPE, messageType);
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            conversion.put(Constants.KEY_LAST_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            addConversion(conversion);
        }

        ////// send notification
        if(!isReceiverAvailable){
            try{
                JSONArray tokens = new JSONArray();
                tokens.put(uReceiver.getToken());

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, preferenceManager.getString(Constants.KEY_MESSAGE));

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
            }catch (Exception e){
                MyUtilities.showToast(getApplicationContext(), e.getMessage());
                e.printStackTrace();
            }
        }

        binding.edittextChatMessage.setText(null); // làm mới edittext nhập tin nhắn
        binding.imgPreview.setImageBitmap(null);
        binding.layoutImgPreview.setVisibility(View.GONE);
        binding.edittextChatMessage.setEnabled(true); // cho phép gõ chữ
        messageType = Constants.KEY_TEXT_MESSAGE; // kiểu text
        binding.layoutRecord.setVisibility(View.GONE);

    }

    /** gửi đoạn ghi âm lên firebase*/
    private void sendAudioToFireBase() {
        String myPath = Constants.KEY_AUDIO_PATH + File.separator + audioName;
        StorageReference storageRef = storage.getReference().child(Constants.KEY_AUDIO_PATH + File.separator + audioName);
        Uri file = Uri.fromFile(new File(audioPath));
        storageRef.putFile(file).addOnFailureListener(e -> {
            MyUtilities.showToast(getApplicationContext(), "Có lỗi khi tải audio lên firebase");
        });
    }

    private void checkForConversion(){
        if(chatList.size() > 0){ /* Nếu có tin nhắn rồi
                                 thực hiện lấy conversionId
                                 thực hiện 2 lần và đảo sender vs receiver vì sender và receiver trong conversion đổi chỗ thay phiên cho nhau
                                 nếu không đảo lại thì có thể bỏ sót */
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    uReceiver.getId()
            );
            checkForConversionRemotely(
                    uReceiver.getId(),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }
    private void checkForConversionRemotely(String senderId, String receiverId){
        //Lấy conversionId với thông tin người gửi và người nhận
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    /** lấy conversionId */
    private OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if( task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };

    /** Thêm hàm kiểm tra trạng thái hoạt động khi resume*/
    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }

    /** Hàm tạo dialog để chọn cách gửi hình ảnh */
    private void openDialogSendPhoto() {
        DialogChooseSendPhotoMethodBinding binding = DialogChooseSendPhotoMethodBinding.inflate(getLayoutInflater());
        PhotoDialog dialog = new PhotoDialog(binding);

        binding.layoutGallery.setOnClickListener(v -> {
            openChooseImage();
            dialog.dismiss();
        });

        binding.layoutCamera.setOnClickListener(v -> {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){ // Kiểm tra nếu điện thoại có camera thì thực hiện chụp ảnh
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mActivityResultLauncher.launch(intent);
                dialog.dismiss();
            }else{
                MyUtilities.showToast(getApplicationContext(), Constants.MESSAGE_NO_CAMERA);
            }
        });

        binding.txtCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show(getSupportFragmentManager(), "dialog");
    }

    /** Tạo Dialog để hiện thị cho người dùng chọn ảnh từ thư viện hay chụp ảnh mới */
    public static class PhotoDialog extends DialogFragment {
        DialogChooseSendPhotoMethodBinding binding;
        public PhotoDialog(DialogChooseSendPhotoMethodBinding binding){
            this.binding = binding;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(binding.getRoot());

            return builder.create();
        }
    }

    private void showMenu(View view){
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);//View will be an anchor for PopupMenu
        popupMenu.inflate(R.menu.chat_options);
        Menu menu = popupMenu.getMenu();
        popupMenu.setOnMenuItemClickListener(v ->{
            if(v.getItemId() == menu.getItem(0).getItemId()){
                MyUtilities.showToast(getApplicationContext(), "Hiện danh sách hình ảnh trong cuộc trò chuyện");
            }
            return true;
        });
        popupMenu.show();
    }

    @Override
    protected void onPause() {
        if(mRecorder != null){
            if(stopRecord()) {
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();
            }
        }
        super.onPause();
    }
}
