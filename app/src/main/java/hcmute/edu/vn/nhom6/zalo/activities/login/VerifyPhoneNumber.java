package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import hcmute.edu.vn.nhom6.zalo.activities.profile.ChangePasswordActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ActivityVerifyPhoneNumberBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.OnVerifySuccess;

public class VerifyPhoneNumber extends AppCompatActivity {
    ActivityVerifyPhoneNumberBinding binding;
    PhoneAuthCredential credential;
    private FirebaseAuth mAuth;
    int INTENT_CODE;

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

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        try {
//            credential = PhoneAuthProvider.getCredential(getIntent().getStringExtra("verificationId"), binding.inputOtp.getText().toString());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information4Log.e("VerifyPhone", "signInWithCredential:success");
//                            String phone = getIntent().getStringExtra(Constants.KEY_PHONE_NUMBER);
//                            Intent intent = new Intent(getApplicationContext(), CreateAccount.class);
//
//                            intent.putExtra(Constants.KEY_PHONE_NUMBER, phone);
//                            startActivity(intent);
////                            FirebaseUser user = task.getResult().getUser();
//                            // Update UI
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.e("VerifyPhone", "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }

}