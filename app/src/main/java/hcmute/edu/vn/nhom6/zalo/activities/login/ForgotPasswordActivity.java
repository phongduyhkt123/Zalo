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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import hcmute.edu.vn.nhom6.zalo.databinding.ActivityForgotPasswordBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.OnVerifySuccess;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        setListeners();
    }

    private void setListeners() {
        binding.textSignIn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignInActivity.class)));

        binding.buttonNext.setOnClickListener(v -> {
            if(isValidSignInInfo()) {
                String phone = MyUtilities.formatPhoneAddHead(binding.inputPhoneNumber.getText().toString());
                prepareSendOTP(phone);
            }
        });
    }

    private void prepareSendOTP(String phone) {
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_PHONE_NUMBER, phone)
                .get()
                .addOnSuccessListener(result -> {
                    if(result != null
                            && result.getDocuments() != null
                            && result.getDocuments().size() > 0){
                        DocumentSnapshot i = result.getDocuments().get(0);
                        sendOTP(phone);
                        preferenceManager.putString(Constants.KEY_IMAGE, i.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_NAME, i.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_USER_ID, i.getId());

                        MyUtilities.showToast(getApplicationContext(), "Đã gửi OTP!");
                    }else{
                        MyUtilities.showToast(getApplicationContext(), "Số điện thoại không tồn tại!");
                    }
                })
                .addOnFailureListener(e -> {
                   MyUtilities.showToast(getApplicationContext(), "Có lỗi xảy ra!");
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
                                Log.e("ForgetPassword sendOTP","onVerificationCompleted");
                                OnVerifySuccess.goToChangePassword(getApplicationContext(), phone);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e("ForgetPassword sendOTP","onVerificationFailed - this is log: "+ e.getMessage().toString());
                                e.printStackTrace();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Log.e("ForgetPassword sendOTP","onCodeSent - this is log");
                                //                              super.onCodeSent(s, forceResendingToken);
                                Intent intent = new Intent(getApplicationContext(), VerifyPhoneNumber.class);
                                intent.putExtra(Constants.KEY_PHONE_NUMBER, phone);
                                intent.putExtra(Constants.KEY_INTENT_TO_VERIFY, Constants.KEY_CHANGE_PASSWORD_INTENT);
                                intent.putExtra("verificationId", s);
                                startActivity(intent);
                            }
                        }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private Boolean isValidSignInInfo(){
        if(binding.inputPhoneNumber.getText().toString().isEmpty()) {
            MyUtilities.showToast(getApplicationContext(), "Vui lòng nhập số điện thoại!");
            return false;
        }
        return true;
    }
}