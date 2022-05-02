package hcmute.edu.vn.nhom6.zalo.activities.mess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.adapters.UsersAdapter;
import hcmute.edu.vn.nhom6.zalo.databinding.FragmentMessBinding;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class MessFragment extends ListFragment {

    private FragmentMessBinding binding;
    private PreferenceManager preferenceManager;

    ArrayList<User> users = new ArrayList<>();

    UsersAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        users.add(new User("Khoa", R.drawable.change_icon_me, "Xong rồi nha bạn", "5 phút"));
//        users.add(new User("Hậu",  R.drawable.change_icon_me, "Đúng rồi đó", "1 giờ"));
//        users.add(new User("Như",  R.drawable.change_icon_me, "Okay nha", "1 ngày"));

        adapter = new UsersAdapter(getActivity(), users);
        return inflater.inflate(R.layout.fragment_mess, container, false);

    }

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = FragmentMessBinding.inflate(getLayoutInflater());
//        View root = binding.getRoot();
//
//        preferenceManager = new PreferenceManager(getActivity().getApplicationContext())
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private void getUser() {
//        loading(true);
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        database.collection(Constants.KEY_COLLECTION_USERS)
//                .get()
//                .addOnCompleteListener(task -> {
//                    loading(false);
//                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
//                    if(task.isSuccessful() && task.getResult() != null) {
//                        List<User> users = new ArrayList<>();
//                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                           if(currentUserId.equals(queryDocumentSnapshot.getId())) {
//                               continue;
//                           }
//                           User user = new User();
//                           user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
//                           user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
//                           user.setPhoneNumber(queryDocumentSnapshot.getString(Constants.KEY_PHONE_NUMBER));
//                           user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
//                           users.add(user);
//                        }
//                        if(users.size() > 0) {
//                            UsersAdapter usersAdapter = new UsersAdapter(users);
//                            binding.usersRecyclerView.setAdapter(usersAdapter);
//                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
//                        } else {
//                            showErrorMessage();
//                        }
//                    } else {
//                        showErrorMessage();
//                    }
//                });
//    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Không có tin nhắn nào"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

//    private void addDataToFirebaseStore(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name", "Duy");
//        data.put("last_name", "Pham");
//        db.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getContext(), "inserted", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//
//    }
}