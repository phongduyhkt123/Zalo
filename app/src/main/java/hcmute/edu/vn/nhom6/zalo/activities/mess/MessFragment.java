package hcmute.edu.vn.nhom6.zalo.activities.mess;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import hcmute.edu.vn.nhom6.zalo.activities.BaseFragment;
import hcmute.edu.vn.nhom6.zalo.adapters.RecentMessageAdapter;
import hcmute.edu.vn.nhom6.zalo.databinding.FragmentMessBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.ConversionListener;
import hcmute.edu.vn.nhom6.zalo.models.ChatMessage;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class MessFragment extends BaseFragment/*with user availability*/ implements ConversionListener {

    private FragmentMessBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<ChatMessage> conversations;
    private RecentMessageAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessBinding.inflate(inflater, container, false);
        init();
        listenConversations();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void init(){
        preferenceManager = new PreferenceManager(getContext());
        conversations = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        adapter = new RecentMessageAdapter(conversations, this);
        binding.usersRecyclerView.setAdapter(adapter);
    }

    private EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if( error != null)
            return;
        if( value != null){
            for( DocumentChange i : value.getDocumentChanges()){
                if(i.getType() == DocumentChange.Type.ADDED){
                    String senderId = i.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = i.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.setConversionImg(i.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversionName(i.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(i.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    }else{
                        chatMessage.setConversionImg(i.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        chatMessage.setConversionName(i.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConversionId(i.getDocument().getString(Constants.KEY_SENDER_ID));
                    }
                    chatMessage.setMessage(i.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(i.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.setType(i.getDocument().getString(Constants.KEY_MESSAGE_TYPE));
                    conversations.add(chatMessage);
                }else if(i.getType() == DocumentChange.Type.MODIFIED){
                    for(int x = 0; x < conversations.size(); x++){
                        String senderId = i.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = i.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(x).getSenderId().equals(senderId)
                                && conversations.get(x).getReceiverId().equals(receiverId)){
                            conversations.get(x).setMessage(i.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            conversations.get(x).setDateObject(i.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            conversations.get(x).setType(i.getDocument().getString(Constants.KEY_MESSAGE_TYPE));
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (x, y) -> y.getDateObject().compareTo(x.getDateObject()));
            adapter.notifyDataSetChanged();
            if(binding != null) {
                binding.usersRecyclerView.smoothScrollToPosition(0);
                binding.usersRecyclerView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    };

    private void listenConversations(){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

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

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        getActivity().startActivity(intent);
    }
}