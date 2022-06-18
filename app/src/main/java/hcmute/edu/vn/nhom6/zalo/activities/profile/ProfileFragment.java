package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import hcmute.edu.vn.nhom6.zalo.activities.BaseFragment;
import hcmute.edu.vn.nhom6.zalo.activities.login.BeforeSignIn;
import hcmute.edu.vn.nhom6.zalo.databinding.FragmentProfileBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** fragment trang cài đặt người dùng */
public class ProfileFragment extends BaseFragment {
    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager; // sharedPreference

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        preferenceManager = new PreferenceManager(getContext());
        setListeners();
        return root;
    }

    private void setListeners() {

        // mở trang quản lý tài khoản
        binding.txtAccount.setOnClickListener(t -> {
            Intent intent = new Intent(ProfileFragment.this.getContext(), AccountActivity.class );
            startActivity(intent);
        });
        // mở trang thiết lập thời gian tự động xóa file
        binding.txtClearCycle.setOnClickListener(t -> {
            Intent intent = new Intent(ProfileFragment.this.getContext(), DeleteCycleActivity.class );
            startActivity(intent);
        });

        // đăng xuất
        binding.txtLogout.setOnClickListener(v -> {
            signOut();
        });

    }

    /** hàm đăng xuất người dùng */
    private void signOut() {
        MyUtilities.showToast(getContext(), "Signing out ...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete()); // xóa giá trị token của người dùng để người dùng ko nhận thông báo tin nhắn
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear(); // xóa dữ liệu đăng nhập sharedPreference
                    preferenceManager.clearRememberSignIn(); // xóa dữ liệu ghi nhớ đăng nhập
                    startActivity(new Intent(getContext(), BeforeSignIn.class)); // mở lại trang đầu của ứng dụng
                    getActivity().finish();
                })
                .addOnFailureListener(e ->{
                    MyUtilities.showToast(getContext(), "Có lỗi xảy ra!");
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
