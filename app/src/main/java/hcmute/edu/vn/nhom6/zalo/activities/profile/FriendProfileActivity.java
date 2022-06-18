package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.activities.mess.ChatActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.ActivityInfoBinding;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

/** Trang profile của bạn bè */
public class FriendProfileActivity extends BaseActivity {
    private ActivityInfoBinding binding;
    private User user; // đây là trang profile của người dùng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER); // lấy user từ intent

        // điền thông tin của user
        binding.imgAvatar.setImageBitmap(MyUtilities.decodeImg(user.getImage()));
        binding.textName.setText(user.getName());
    }
    private void setListeners() {
        binding.imgBack.setOnClickListener( v -> onBackPressed());

        binding.btnMess.setOnClickListener( v -> openChat(user));
    }

    /** Mở trang chat của người dùng hiện tại với người dùng này*/
    private void openChat(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }


}