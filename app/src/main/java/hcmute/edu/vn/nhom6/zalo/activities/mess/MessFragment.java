package hcmute.edu.vn.nhom6.zalo.activities.mess;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

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

        // Tạo đường phân cách giữa các item trong recycleView
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        binding.usersRecyclerView.addItemDecoration(itemDecoration);
    }

    /** lắng nghe sự thay đổi csdl ở hàm listenConversations
     * tiến hành thay đổi dữ liệu cho RecycleView để cập nhật lại recycleView
    * value là dòng dữ liệu của bảng conversations có senderId bằng với userId hiện tại hoặc
    * dòng dữ liệu của bảng conversations có receiverId bằng với userId hiện tại
    * gồm lastMessage, receiverId, receiverImage, senderId, senderImage, ...
    * với lastMessage là tin nhắn mới nhất
    *       sender là người bắt đầu cuộc trò chuyện (người gửi tin nhắn đầu tiên)
    *       receiver là người nhận tin nhắn từ sender*/
    private EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if( error != null)
            return;
        if( value != null){
            for( DocumentChange i : value.getDocumentChanges()){
                if(i.getType() == DocumentChange.Type.ADDED){ /* nếu dòng dữ liệu này mới được thêm vào*/
                    String senderId = i.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = i.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);
                    /*xét nếu current user là sender thì thông tin hiển thị của conversation trên RecycleView là thông tin của receiver
                    * Ngược lại nếu current user là receiver thì thông tin hiển thị là của sender*/
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){ /*Nếu người dùng hiện tại là sender*/
                        chatMessage.setConversionImg(i.getDocument().getString(Constants.KEY_RECEIVER_IMAGE)); // Lấy hình ảnh hiện thị là receiverImg
                        chatMessage.setConversionName(i.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(i.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    }else{/*Ngược lại, nếu người dùng hiện tại là người nhận*/
                        chatMessage.setConversionImg(i.getDocument().getString(Constants.KEY_SENDER_IMAGE)); // Lấy hình ảnh hiện thị là senderImg
                        chatMessage.setConversionName(i.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConversionId(i.getDocument().getString(Constants.KEY_SENDER_ID));
                    }
                    // lấy các thông tin còn lại
                    chatMessage.setLastSenderId(i.getDocument().getString(Constants.KEY_LAST_SENDER_ID));
                    chatMessage.setMessage(i.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(i.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.setType(i.getDocument().getString(Constants.KEY_MESSAGE_TYPE));

                    //Thêm chatMessage vào conversations để hiển thị vào recycleView
                    conversations.add(chatMessage);

                }else if(i.getType() == DocumentChange.Type.MODIFIED){ // Nếu dòng dữ liệu này được thay đổi (lastMessage)
                    //
                    String senderId = i.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = i.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    /* Tìm trong conversations list cái conversation nào mà có sender là sender của dòng dữ liệu mới được modified
                    * Mục đích là để thay đổi các giá trị lastMessage,... của conversation này để cập nhật trên RecycleView*/
                    for(int x = 0; x < conversations.size(); x++){
                        if(conversations.get(x).getSenderId().equals(senderId)               /*Nếu sender của conversation này là sender của dòng dữ liệu*/
                                && conversations.get(x).getReceiverId().equals(receiverId)){ /*và receiver của conversation này là receiver của dòng dữ liệu*/
                                                                                             /*thì cập nhật lại thông tin của conversation*/
                            conversations.get(x).setMessage(i.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            conversations.get(x).setDateObject(i.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            conversations.get(x).setType(i.getDocument().getString(Constants.KEY_MESSAGE_TYPE));
                            break;
                        }
                    }
                }
            }

            //Sắp xếp các conversation theo thời gian (mới nhất lên trên)
            Collections.sort(conversations, (x, y) -> y.getDateObject().compareTo(x.getDateObject()));
            adapter.notifyDataSetChanged(); // gọi cập nhật recycleView
            if(binding != null) {
                binding.usersRecyclerView.smoothScrollToPosition(0); // cuộn lên đầu
                binding.usersRecyclerView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    };

    /** Thực hiện cài đặt sự kiện khi có tin nhắn đến hoặc khi gửi tin nhắn thì cập nhật thông tin conversation ở messFragment*/
    private void listenConversations(){
        /*Cần phải xét 2 trường hợp một là sender, một là receiver vì current user có trong cuộc hội thoại thì cần phải cập nhật thông tin*/

        /*Khi các dòng dữ liệu của bảng conversations có senderId bằng với userId hiện tại thì thực hiện cập nhật trong eventListener*/
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(/*getActivity(),*/ eventListener); // lắng nghe thay đổi trên csdl firebase

        /*Khi các dòng dữ liệu của bảng conversations có receiverId bằng với userId hiện tại thì thực hiện cập nhật trong eventListener*/
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(/*getActivity(),*/ eventListener);

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

    /** Khi nhấn vào cuộc trò chuyện thì mở màn hình chatActivity */
    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user); // truyền người nhận vào
        getActivity().startActivity(intent);
    }
}