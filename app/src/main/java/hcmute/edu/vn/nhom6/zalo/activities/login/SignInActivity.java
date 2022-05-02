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

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.buttonSignUp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(v ->{
            signIn();
        });

        binding.textForgotPassword.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));
    }

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

    private void signIn(){
        loading(true);
        if(isValidSignInInfo()){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_PHONE_NUMBER, binding.inputPhoneNumber.getText().toString().trim())
                    .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString().trim())
                    .get()
                    .addOnCompleteListener(/*OnCompleteListener.onComplete*/task ->{
                       if(task.isSuccessful()
                               && task.getResult() != null
                               && task.getResult().getDocuments().size()>0){
                           DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                           preferenceManager.putSignInInfo(
                               documentSnapshot.getId(),
                               documentSnapshot.getString(Constants.KEY_PHONE_NUMBER),
                               documentSnapshot.getString(Constants.KEY_NAME),
                               documentSnapshot.getString(Constants.KEY_IMAGE)
                           );

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