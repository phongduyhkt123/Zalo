package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.nhom6.zalo.databinding.AccountSittingBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class AccountActivity extends AppCompatActivity {
    private AccountSittingBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccountSittingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        preferenceManager = new PreferenceManager(getApplicationContext());
        setData();
        setListeners();
    }

    private void setData() {
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            binding.ivAvt.setImageBitmap(
                    MyUtilities.decodeImg(preferenceManager.getString(Constants.KEY_IMAGE)));
            binding.txtName.setText(preferenceManager.getString(Constants.KEY_NAME));
            binding.txtPhone.setText(preferenceManager.getString(Constants.KEY_PHONE_NUMBER));
        }
    }

    private void setListeners() {
        binding.layoutPhone.setOnClickListener(t -> {
            Intent intent = new Intent(AccountActivity.this, ChangePhoneActivity.class );
            startActivity(intent);
        });

        binding.txtChangePassword.setOnClickListener(t -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class );
            startActivity(intent);
        });

        binding.back.setOnClickListener(t -> { finish(); });
    }
}