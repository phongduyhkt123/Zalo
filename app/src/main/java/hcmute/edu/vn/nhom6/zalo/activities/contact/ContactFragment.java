package hcmute.edu.vn.nhom6.zalo.activities.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.databinding.FragmentContactBinding;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

public class ContactFragment extends Fragment {
    private FragmentContactBinding binding;
    private Map<String, ArrayList<User>> groupList = new HashMap<>();
    private GroupAdapter groupAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ContactViewModel dashboardViewModel =
        new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setData();

        groupAdapter = new GroupAdapter(container.getContext(), groupList);
        binding.rv1.setAdapter(groupAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rv1.setLayoutManager(layoutManager);

        return root;
    }

    private void setData(){
        ArrayList<User> contactList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).get()
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()
                    && task.getResult() != null
                    && task.getResult().getDocuments().size() > 0){
                for(QueryDocumentSnapshot i: task.getResult()) {
                    contactList.add(new User(
                            i.getString(Constants.KEY_NAME),
                            i.getString(Constants.KEY_PHONE_NUMBER),
                            i.getString(Constants.KEY_IMAGE)
                    ));
                }
                groupList.put(Constants.KEY_ALL_CONTACT, contactList);
                groupAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            MyUtilities.showToast(getContext(), "Không thể tải danh bạ: "+ e.getMessage());
            e.printStackTrace();
        });
//        contactList.add(new User("Phong Duy", "7450736856"));
//        contactList.add(new User("ABC", "5437475647"));
//        contactList.add(new User("ABd", "5437475647"));
//        contactList.add(new User("ABe", "5437475647"));
//        contactList.add(new User("ABf", "5437475647"));
//        contactList.add(new User("ABg", "5437475647"));

//        ArrayList<User> bfList = new ArrayList<>();
//        bfList.add(new User("ABC", "5437475647"));
//        bfList.add(new User("ABd", "5437475647"));
//        bfList.add(new User("ABf", "5437475647"));
//
//        groupList.put("bf", bfList);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showErrorMessage(){

    }
}