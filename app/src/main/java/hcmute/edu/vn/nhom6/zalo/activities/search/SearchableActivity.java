package hcmute.edu.vn.nhom6.zalo.activities.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.activities.contact.RowContactAdapter;
import hcmute.edu.vn.nhom6.zalo.activities.profile.FriendProfileActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.SearchBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.UserListener;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;


public class SearchableActivity extends BaseActivity implements UserListener {
    SearchBinding binding;
    FirebaseFirestore db;
    ArrayList<User> userList;
    PreferenceManager preferenceManager;
    RowContactAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the intent, verify the action and get the query
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        userList = new ArrayList<User>();
        getAllUser();
        Intent intent = getIntent();

        adapter = new RowContactAdapter(
                userList,
                this
        );


        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.rvUser.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvUser.setLayoutManager(layoutManager);

        setListeners();

        // Tạo đường phân cách giữa các item trong recycleView
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        binding.rvUser.addItemDecoration(itemDecoration);

    }

    private void setListeners() {
        binding.topAppBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void getAllUser(){
        db.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnSuccessListener(result -> {
                    ArrayList<User> tempList = new ArrayList<>();
                    if(result != null && result.getDocuments() != null && result.getDocuments().size() >0){
                        for (QueryDocumentSnapshot i: result) {
                            if(i.getString(Constants.KEY_PHONE_NUMBER).equals(preferenceManager.getString(Constants.KEY_PHONE_NUMBER))){
                                continue;
                            }
                            tempList.add(new User(
                                    i.getId(),
                                    i.getString(Constants.KEY_NAME),
                                    i.getString(Constants.KEY_PHONE_NUMBER),
                                    i.getString(Constants.KEY_FCM_TOKEN),
                                    i.getString(Constants.KEY_IMAGE)
                            ));
                        }

                        userList = tempList;
                        adapter.setDataList(userList);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener( e -> {
                   e.printStackTrace();
                    MyUtilities.showToast(getApplicationContext(), "không lấy được danh sách người dùng");
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); // lấy search key
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty())
                    adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty())
                    adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), FriendProfileActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}
