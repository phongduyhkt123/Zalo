package hcmute.edu.vn.nhom6.zalo.activities;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.activities.search.SearchableActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ActivityMainBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class MainActivity extends BaseActivity/*with user availability*/ {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseStorage storage; // storage của firebase để chứa file
    private FirebaseFirestore db; // csdl firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mess, R.id.navigation_contact, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        db = FirebaseFirestore.getInstance();

        setListeners();

        getToken();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            deleteImageAndAudio();
        }
    }

    private void setListeners() {
        binding.topAppBar.setOnClickListener( v -> {
            startActivity(new Intent(getApplicationContext(), SearchableActivity.class));
        });
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
//        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(task -> updateToken(task));
    }

    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);

        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> {
                    MyUtilities.showToast(getApplicationContext(), "Token updated successfully!");
                })
                .addOnFailureListener(e ->{
                    e.printStackTrace();
                    MyUtilities.showToast(getApplicationContext(), "Failed when update token: "+ e.getMessage());
                });
    }

    /** Xóa file quá thời hạn */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void deleteImageAndAudio() {
        int lifeCycleDay = preferenceManager.getInt(Constants.KEY_DELETE_PERIOD);
        if (lifeCycleDay == -1)
            return; // nếu người dùng không thiết lập thì không thực hiện xóa file
        updateToFirebase(lifeCycleDay);

    }
    /* HÀM NÀY CHƯA DÙNG ĐẾN*/
    /** Hàm xóa file trong thư mục với thời gian tồn tại quá thời hạn*/
    /* Tìm ngày x cách hiện tại lifeCycle ngày
    * Sau đó xét các file trong thư mục
    *  nếu ngày tạo file mà trước ngày x thì xóa file đó */
    public ArrayList<String> deleteFilesInDir(File dir, /* số ngày tồn tại */ int lifeCycle ) {
        if (dir == null)
            return null;

        ArrayList<String> filesName = new ArrayList<>();

        /*Tạo một stack để lưu các thư mục
        đầu tiên là thư mục gốc,
        nếu trong thư mục gốc có thư mục con thì tiếp tục thêm thư mục con vào stack này
        và lập lại */
        Stack<File> dirList = new Stack<File>();
        dirList.clear();
        dirList.push(dir);
        while (!dirList.isEmpty()) {
            File dirCurrent = dirList.pop();

            File[] fileList = dirCurrent.listFiles(); // lấy danh sách các File trong thư mục
            for (File file : fileList) {
                if (file.isDirectory()) // File này là thư mục
                    dirList.push(file); // thêm File này vào stack thư mục
                else{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) { // phiên bản >= 24
                        Calendar time = Calendar.getInstance(); // Lấy thời gian hiện tại với calendar
                        time.add((Calendar.DAY_OF_YEAR), -lifeCycle); // lấy ngược lại lifeCycle ngày
                        Date lastModified = new Date(file.lastModified()); // lấy thời gian tạo file
                        if (lastModified.before(time.getTime())){ // file quá thời hạn
                            filesName.add(file.getName());
                            file.delete(); // xóa file
                        }
                    }
                }
            }
        }
        return filesName;
    }


    /** hàm xòa file, cập nhật giá trị isStored của tin nhắn lên Firebase để nói rằng file đã bị tự động xóa, sẽ không tải về nữa
     * Nội dung là :
     * truy vấn tin nhắn mà người dùng hiện tại là sender xét tin nhắn là dạng image hoặc audio thì xóa file trong máy (nếu có) và cập nhật isStore lên firebase
     * tương tự với tin nhắn mà người dùng hiện tại là receiver*/
    @RequiresApi(api = Build.VERSION_CODES.N) // áp dụng cho phiên bản 24 trở lên
    private void updateToFirebase(int lifeCycle){
        Calendar time = Calendar.getInstance(); // Lấy thời gian hiện tại với calendar
        time.add((Calendar.DAY_OF_YEAR), -lifeCycle); // lấy ngược lại lifeCycle ngày
        // Lấy tin nhắn current user là người gửi
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnSuccessListener( querySnapshot -> {
                    if( querySnapshot.getDocuments() != null && querySnapshot.getDocuments().size() > 0){
                        for (DocumentSnapshot i: querySnapshot.getDocuments()) {
                            if(i.getBoolean(Constants.KEY_IS_STORED_SENDER)){ // tin nhắn chưa tự động xóa
                                if(i.getDate(Constants.KEY_TIMESTAMP).before(time.getTime())){ // nếu tin nhắn quá hạn thì xóa
                                    if(i.getString(Constants.KEY_MESSAGE_TYPE).equals(Constants.KEY_PICTURE_MESSAGE)){
                                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                                                Constants.KEY_IMAGE_PATH + File.separator + i.getString(Constants.KEY_MESSAGE));
                                        if(file.exists()){
                                            file.delete(); // xóa file
                                        }
                                    }else if(i.getString(Constants.KEY_MESSAGE_TYPE).equals(Constants.KEY_AUDIO_MESSAGE)){
                                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                                                Constants.KEY_AUDIO_PATH + File.separator + i.getString(Constants.KEY_MESSAGE));
                                        if(file.exists()){
                                            file.delete();
                                        }
                                    }
                                    updateIsStore(i.getId(), Constants.KEY_IS_STORED_SENDER);
                                }
                            }
                        }
                    }

                    // Lấy tin nhắn current user là người nhận
                    db.collection(Constants.KEY_COLLECTION_CHAT)
                            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                            .get()
                            .addOnSuccessListener(querySnapshot2 -> {
                                if (querySnapshot2.getDocuments() != null && querySnapshot2.getDocuments().size() > 0) {
                                    for (DocumentSnapshot i : querySnapshot2.getDocuments()) {
                                        if(i.getBoolean(Constants.KEY_IS_STORED_RECEIVER)){ // tin nhắn chưa tự động xóa
                                            if(i.getDate(Constants.KEY_TIMESTAMP).before(time.getTime())){ // nếu tin nhắn quá hạn thì xóa
                                                if(i.getString(Constants.KEY_MESSAGE_TYPE).equals(Constants.KEY_PICTURE_MESSAGE)){
                                                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                                                            Constants.KEY_IMAGE_PATH + File.separator + i.getString(Constants.KEY_MESSAGE));
                                                    if(file.exists()){
                                                        file.delete(); // xóa file
                                                    }
                                                }else if(i.getString(Constants.KEY_MESSAGE_TYPE).equals(Constants.KEY_AUDIO_MESSAGE)){
                                                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                                                            Constants.KEY_IMAGE_PATH + File.separator + i.getString(Constants.KEY_MESSAGE));
                                                    if(file.exists()){
                                                        file.delete();
                                                    }
                                                }
                                                updateIsStore(i.getId(), Constants.KEY_IS_STORED_RECEIVER);
                                            }
                                        }
                                    }
                                }
                                /* if (list.size() > 0){

                                    for (ChatMessage message: list ) {
                                        if (message.getType().equals(Constants.KEY_PICTURE_MESSAGE)){
                                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_IMAGE_PATH + File.separator + message.getMessage());
                                            if(file.exists()){
                                                file.delete();
                                            }
                                        } else {
                                            if (message.getType().equals(Constants.KEY_PICTURE_MESSAGE)) {
                                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            }
                                        }
                                    }

                                }*/
                            })
                            .addOnFailureListener(e -> {

                            });
                }).addOnFailureListener(e -> {

                });
    }

    private void updateIsStore(String messageId, String userField){
        db.collection(Constants.KEY_COLLECTION_CHAT).document(messageId).update(
                userField, false
        );
    }
}