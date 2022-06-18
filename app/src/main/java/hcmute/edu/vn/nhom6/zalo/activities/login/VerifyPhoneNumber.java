package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import hcmute.edu.vn.nhom6.zalo.databinding.ActivityVerifyPhoneNumberBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.OnVerifySuccess;

/** Activity xác thực số điện thoại */
public class VerifyPhoneNumber extends AppCompatActivity {
    ActivityVerifyPhoneNumberBinding binding;
    PhoneAuthCredential credential;
    private FirebaseAuth mAuth;
    int INTENT_CODE; // có thể đi đến tạo tài khoản hoặc đổi mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyPhoneNumberBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        INTENT_CODE = getIntent().getIntExtra(Constants.KEY_INTENT_TO_VERIFY, 0);
        setListener();
    }

    public void setListener(){

        binding.buttonNext.setOnClickListener(v -> {
            // tiến hành xác nhận OTP
            OnVerifySuccess.signInWithPhoneAuthCredential(
                    credential,
                    getIntent(),
                    getBaseContext(),
                    mAuth,
                    this,
                    binding.inputOtp.getText().toString(),
                    INTENT_CODE// có thể đi đến tạo tài khoản hoặc đổi mật khẩu
            );});
    }

}