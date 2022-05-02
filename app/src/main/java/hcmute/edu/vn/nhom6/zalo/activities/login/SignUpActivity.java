package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.nhom6.zalo.databinding.ActivitySignUpBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.OnVerifySuccess;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setListeners();
    }

    private void setListeners() {
        binding.textHadAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
        binding.buttonSignUp.setOnClickListener(v -> {
            sendOTP(binding.inputPhoneNumber.getText().toString());
        });
    }
    //Gửi OTP tới số điện thoại
    public void sendOTP(String phone){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            // Trường hợp điện thoại tự động xác nhận thành công
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.e("SignUpActivity sendOTP","onVerificationCompleted");
                                OnVerifySuccess.goToCreateAccount(getApplicationContext(), phone);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e("SignUpActivity sendOTP","onVerificationFailed - this is log: "+ e.getMessage().toString());
                                e.printStackTrace();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Log.e("SignUpActivity sendOTP","onCodeSent - this is log");
                                //                              super.onCodeSent(s, forceResendingToken);
                                Intent intent = new Intent(getApplicationContext(), VerifyPhoneNumber.class);
                                intent.putExtra(Constants.KEY_PHONE_NUMBER, phone);
                                intent.putExtra("verificationId", s);
                                startActivity(intent);
                            }
                        }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}