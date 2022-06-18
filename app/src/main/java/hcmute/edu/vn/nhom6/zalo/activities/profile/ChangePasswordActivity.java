package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ConcurrentModificationException;

import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ChangePasswordBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** Activity đổi mật khẩu */
public class ChangePasswordActivity extends BaseActivity {
    ChangePasswordBinding binding;
    FirebaseFirestore db; // csdl
    PreferenceManager preferenceManager; // sharedPreference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        binding.txtSubmit.setOnClickListener(v -> {
            if(isValidInfo()){
                updatePassword();
            }
        });
    }

    /** Hàm đổi mật khẩu */
    private void updatePassword() {
        String password = binding.txtNewPassword.getText().toString();
        // đổi mật khẩu trên csdl
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(
                        Constants.KEY_PASSWORD, password
                )
                .addOnSuccessListener(t -> {
                    // cập nhật mật khẩu trên sharedPreference
                    preferenceManager.putString(Constants.KEY_PASSWORD, password);
                    MyUtilities.showToast(getApplicationContext(), "Đổi mật khẩu thành công");
                })
                .addOnFailureListener(e -> {
                    MyUtilities.showToast(getApplicationContext(), "Có lỗi xảy ra!");
                    e.printStackTrace();
                });
    }

    /** Kiểm tra thông tin nhập vào có hợp lệ không */
    private boolean isValidInfo() {
        if(binding.txtNewPassword.getText().toString().isEmpty()){
            MyUtilities.showToast(getApplicationContext(), "Hãy nhập mật khẩu mới!");
            binding.txtNewPassword.requestFocus();
            return false;
        }
        if(binding.txtOldPassword.getText().toString().isEmpty()){
            MyUtilities.showToast(getApplicationContext(), "Hãy nhập mật khẩu cũ!");
            binding.txtOldPassword.requestFocus();
            return false;
        }
        if(binding.txtConfirm.getText().toString().isEmpty()){
            MyUtilities.showToast(getApplicationContext(), "Hãy nhập lại mật khẩu mới!");
            binding.txtConfirm.requestFocus();
            return false;
        }
        if(!binding.txtConfirm.getText().toString()
                .equals(binding.txtNewPassword.getText().toString())){
            MyUtilities.showToast(getApplicationContext(), "Nhập lại mật khẩu không đúng!");
            binding.txtConfirm.requestFocus();
            return false;
        }
        if(!checkPassword(binding.txtOldPassword.getText().toString())){
            MyUtilities.showToast(getApplicationContext(), "Mật khẩu không đúng!");
            binding.txtOldPassword.requestFocus();
            return false;
        }
        return true;
    }

    /** Kiểm tra nhập mật khẩu cũ có đúng không */
    private boolean checkPassword(String password) {
        if(preferenceManager.getString(Constants.KEY_PASSWORD).equals(password))
            return true;
        return false;
    }

}