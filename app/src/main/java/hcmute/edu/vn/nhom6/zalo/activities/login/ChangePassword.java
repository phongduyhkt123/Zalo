package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.databinding.ActivityChangePasswordBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ChangePasswordBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** Đổi mật khẩu lúc quên mật khẩu*/
public class ChangePassword extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        db = FirebaseFirestore.getInstance();
        setUserData();
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void setUserData() {
        binding.name.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.imageProfile.setImageBitmap(MyUtilities.decodeImg(preferenceManager.getString(Constants.KEY_IMAGE)));
    }

    private void setListeners() {
        binding.buttonSubmit.setOnClickListener( v -> {
            if(isValidInfo()){
                updatePassword();
            }
        });
    }

    private void updatePassword() {
        String password = binding.inputPassword.getText().toString();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(
                        Constants.KEY_PASSWORD, password
                )
                .addOnSuccessListener(t -> {
                    MyUtilities.showToast(getApplicationContext(), "Đổi mật khẩu thành công");
                })
                .addOnFailureListener(e -> {
                    MyUtilities.showToast(getApplicationContext(), "Có lỗi xảy ra!");
                    e.printStackTrace();
                });
    }

    private boolean isValidInfo() {
        if(binding.inputPassword.getText().toString().isEmpty()){
            MyUtilities.showToast(getApplicationContext(), "Hãy nhập mật khẩu mới!");
            binding.inputPassword.requestFocus();
            return false;
        }

        if(binding.inputConfirmPassword.getText().toString().isEmpty()){
            MyUtilities.showToast(getApplicationContext(), "Hãy nhập lại mật khẩu mới!");
            binding.inputConfirmPassword.requestFocus();
            return false;
        }
        if(!binding.inputConfirmPassword.getText().toString()
                .equals(binding.inputPassword.getText().toString())){
            MyUtilities.showToast(getApplicationContext(), "Nhập lại mật khẩu không đúng!");
            binding.inputConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

}