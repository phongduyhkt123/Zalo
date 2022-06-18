package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import hcmute.edu.vn.nhom6.zalo.activities.MainActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ActivityCreateAccountBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** Activity tạo tài khoản */
public class CreateAccount extends AppCompatActivity {
    private ActivityCreateAccountBinding binding;
    private String encodedImg; // hình ảnh encoded base64
    private PreferenceManager preferenceManager; // sharedPreference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
    }

    private void setListener(){
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignupInfo()){ // kiểm tra thông tin
                signUp(); // Tạo tài khoản
            }
        });

        // chọn hình ảnh từ thư viện để làm ảnh đại diện
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });
    }

    /** Tạo tài khoản */
    private void signUp() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String phone = getIntent().getStringExtra(Constants.KEY_PHONE_NUMBER);
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_PHONE_NUMBER, phone);
        user.put(Constants.KEY_IMAGE, encodedImg);
        user.put(Constants.KEY_DELETE_PERIOD, -1); // thời gian tự động xóa file = -1 là chưa thiết lập

        // Thêm người dùng vào csdl
        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    // Đưa thông tin người dùng lên sharedPreference
                    preferenceManager.putSignInInfo(
                            documentReference.getId(),
                            phone,
                            binding.inputName.getText().toString(),
                            encodedImg,
                            user.get(Constants.KEY_PASSWORD).toString(),
                            -1
                    );

                    // ghi nhớ đăng nhập
                    preferenceManager.putRememberSignIn(
                            phone,
                            user.get(Constants.KEY_PASSWORD).toString()
                    );

                    // mở màn hình chính
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // xóa những activities trước và đưa cái này lên trên
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    MyUtilities.showToast(getApplicationContext(),e.getMessage());
                    e.printStackTrace();
                });
    }


    private Boolean isValidSignupInfo(){ // kiểm tra thông tin
        if(encodedImg == null){
            MyUtilities.showToast(getApplicationContext(),"Vui lòng chọn ảnh đại diện!");
            return false;
        }else if(binding.inputName.getText().toString().trim().isEmpty()){
            MyUtilities.showToast(getApplicationContext(),"Vui lòng nhập họ và tên!");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            MyUtilities.showToast(getApplicationContext(),"Vui lòng nhập mật khẩu!");
            return false;
        }else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            MyUtilities.showToast(getApplicationContext(),"Vui lòng nhập lại mật khẩu!");
            return false;
        }else if(!(binding.inputPassword.getText().toString().trim()
                .equals(binding.inputConfirmPassword.getText().toString().trim()))){
            MyUtilities.showToast(getApplicationContext(), "Nhập lại mật khẩu không đúng");
            return false;
        }

        return true;

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
        }else{
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }

    /** load intent cho người dùng chọn hình ảnh từ thư viện */
    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.txtImage.setVisibility(View.GONE);
                            encodedImg = MyUtilities.encodeImg(bitmap, 200);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
}