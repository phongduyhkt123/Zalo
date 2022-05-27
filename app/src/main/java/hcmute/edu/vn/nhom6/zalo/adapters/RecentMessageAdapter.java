package hcmute.edu.vn.nhom6.zalo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerRecentChatBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.ConversionListener;
import hcmute.edu.vn.nhom6.zalo.models.ChatMessage;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class RecentMessageAdapter extends RecyclerView.Adapter<RecentMessageAdapter.RecentMessageViewHolder>{
    private ArrayList<ChatMessage> chatList;
    private ConversionListener conversionListener;

    public RecentMessageAdapter(ArrayList<ChatMessage> chatList, ConversionListener conversionListener) {
        this.chatList = chatList;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public RecentMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentMessageViewHolder(
                ItemContainerRecentChatBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentMessageViewHolder holder, int position) {
        holder.setData(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class RecentMessageViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentChatBinding binding;

        public RecentMessageViewHolder(ItemContainerRecentChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage chatMessage){
            PreferenceManager preferenceManager = new PreferenceManager(binding.getRoot().getContext());
            binding.ivImage.setImageBitmap(MyUtilities.decodeImg(chatMessage.getConversionImg())); /* Hiển thị hình ảnh với ConversationImg là hình ảnh của receiver (giải thích ở MessageFragment)*/
            String message = "";
            /* Xét xem nếu tin nhắn thuộc loại text thì hiện preview bình thường
            * Nếu tin nhắn thuộc loại hình ảnh thì hiện thông báo gửi hình ảnh
            * Nếu tin nhắn thuộc loại audio thì hiện thông báo gửi tin nhắn thoại*/

            if(chatMessage.getType().equals(Constants.KEY_TEXT_MESSAGE))  // tin nhắn loại text
                message = chatMessage.getMessage();
            else if (chatMessage.getType().equals(Constants.KEY_PICTURE_MESSAGE)) // tin nhắn loại hình ảnh
                message = chatMessage.getSenderId().equals(preferenceManager.getString(Constants.KEY_USER_ID)) ? // Nếu người dùng hiện tại là người gửi tin nhắn
                        Constants.MESSAGE_SENT_A_PICTURE :                  // Hiện thông báo gửi hình ảnh
                        chatMessage.getConversionName() + Constants.MESSAGE_RECEIVED_A_PICTURE; // ngược lại hiện thông báo senderName + gửi hình ảnh
            else if (chatMessage.getType().equals(Constants.KEY_AUDIO_MESSAGE))
                message = chatMessage.getSenderId().equals(preferenceManager.getString(Constants.KEY_USER_ID)) ? // Nếu người dùng hiện tại là người gửi tin nhắn
                        Constants.MESSAGE_SENT_A_AUDIO :
                        chatMessage.getConversionName() + Constants.MESSAGE_RECEIVED_A_AUDIO;
            binding.txtMessage.setText(message);

            binding.txtName.setText(chatMessage.getConversionName());

            /* Cài đặt sự kiện để khi nhấn vào conversation thì mở ChatActivity với interface ConversationLister - onConversationClicked */
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.setId(chatMessage.getConversionId());
                user.setName(chatMessage.getConversionName());
                user.setEncodedImg(chatMessage.getConversionImg());
                conversionListener.onConversionClicked(user);
            });
        }
    }
}
