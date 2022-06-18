package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hcmute.edu.vn.nhom6.zalo.activities.MainActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ActivitySignInBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** Activity đăng nhập */
public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager; // sharedPreference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        rememberSignIn(); // nếu có ghi nhớ đăng nhập thì tiến hành đăng nhập luôn
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.buttonSignUp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        // đăng nhập
        binding.buttonSignIn.setOnClickListener(v ->{
            signIn(
                    binding.inputPhoneNumber.getText().toString().trim(),
                    binding.inputPassword.getText().toString().trim()
            );
        });

        // quên mật khẩu
        binding.textForgotPassword.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));
    }

    /** Ghi nhớ đăng nhập */
    private void rememberSignIn() {
        // nếu có tồn tại thông tin đăng nhập trong sharedPreference thì tiến hành đang nhập luôn
        if(preferenceManager.getString(Constants.KEY_REMEMBER_PHONE) != null &&
                preferenceManager.getString(Constants.KEY_REMEMBER_PASSWORD) != null){
            signIn(
                    preferenceManager.getString(Constants.KEY_REMEMBER_PHONE),
                    preferenceManager.getString(Constants.KEY_REMEMBER_PASSWORD)
            );
        }
    }

    /** Kiểm tra thông tin */
    private Boolean isValidSignInInfo(){
        if(binding.inputPhoneNumber.getText().toString().isEmpty()) {
            MyUtilities.showToast(getApplicationContext(), "Vui lòng nhập số điện thoại!");
            return false;
        }else if(binding.inputPassword.getText().toString().isEmpty()){
            MyUtilities.showToast(getApplicationContext(), "Vui lòng nhập mật khẩu!");
            return false;
        }
        return true;
    }

    /** Đăng nhập */
    private void signIn(String phone, String password){
        loading(true);
        if(isValidSignInInfo()){ // kiểm tra thông tin
            String mPhone = MyUtilities.formatPhoneAddHead(phone);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Tìm trên csdl người dùng có thông tin đăng nhập này
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_PHONE_NUMBER, mPhone)
                    .whereEqualTo(Constants.KEY_PASSWORD, password)
                    .get()
                    .addOnCompleteListener(/*OnCompleteListener.onComplete*/task ->{
                       if(task.isSuccessful()
                               && task.getResult() != null
                               && task.getResult().getDocuments().size()>0){ // nếu có thì đưa thông tin đăng nhập lên sharedPreference
                           DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                           preferenceManager.putSignInInfo(
                               documentSnapshot.getId(),
                               documentSnapshot.getString(Constants.KEY_PHONE_NUMBER),
                               documentSnapshot.getString(Constants.KEY_NAME),
                               documentSnapshot.getString(Constants.KEY_IMAGE),
                               documentSnapshot.getString(Constants.KEY_PASSWORD),
                               documentSnapshot.getLong(Constants.KEY_DELETE_PERIOD)
                           );

                           // ghi nhớ đăng nhập
                           preferenceManager.putRememberSignIn(
                                   documentSnapshot.getString(Constants.KEY_PHONE_NUMBER),
                                   documentSnapshot.getString(Constants.KEY_PASSWORD)
                           );

                           // Vào màn hình chính
                           Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // xóa những activities trước và đưa cái này lên trên
                           startActivity(intent);
                           finish();
                       }else{
                           loading(false);
                           MyUtilities.showToast(getApplicationContext(), "Sai thông tin đăng nhập!");
                       }
                    });
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
        }else{
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }
}