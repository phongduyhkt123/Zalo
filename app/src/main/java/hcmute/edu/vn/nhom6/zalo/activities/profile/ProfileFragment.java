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


public class ProfileFragment extends BaseFragment {
    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        ContactViewModel dashboardViewModel =
//                new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        preferenceManager = new PreferenceManager(getContext());
        setListeners();
        return root;
    }

    private void setListeners() {
        binding.txtAccount.setOnClickListener(t -> {
            Intent intent = new Intent(ProfileFragment.this.getContext(), AccountActivity.class );
            startActivity(intent);
        });

        binding.txtClearCycle.setOnClickListener(t -> {
            Intent intent = new Intent(ProfileFragment.this.getContext(), DeleteCycleActivity.class );
            startActivity(intent);
        });

        binding.txtLogout.setOnClickListener(v -> {
            signOut();
        });

    }

    private void signOut() {
        MyUtilities.showToast(getContext(), "Signing out ...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                db.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getContext(), BeforeSignIn.class));
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
