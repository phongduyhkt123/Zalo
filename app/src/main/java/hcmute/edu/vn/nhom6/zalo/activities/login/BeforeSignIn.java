package hcmute.edu.vn.nhom6.zalo.activities.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.nhom6.zalo.databinding.ActivityBeforeSignInBinding;
import hcmute.edu.vn.nhom6.zalo.firebase.MessagingService;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class BeforeSignIn extends AppCompatActivity {

    private ActivityBeforeSignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        rememberSignIn();
        binding = ActivityBeforeSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void rememberSignIn() {
        if(preferenceManager.getString(Constants.KEY_REMEMBER_PHONE) != null &&
                preferenceManager.getString(Constants.KEY_REMEMBER_PASSWORD) != null){
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
    }

    private void setListeners() {
        binding.buttonSignIn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignInActivity.class)));

        binding.buttonSignUp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
    }

}