package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ChangePhoneBinding;

/** Activity đổi số điện thoại
 * Nhưng chưa xử lý activity này */
public class ChangePhoneActivity extends BaseActivity {
    ChangePhoneBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChangePhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topAppBar.setNavigationOnClickListener(t -> onBackPressed());
    }
}