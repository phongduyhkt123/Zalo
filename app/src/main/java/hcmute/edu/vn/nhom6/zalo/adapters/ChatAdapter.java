package hcmute.edu.vn.nhom6.zalo.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerReceivedMessageBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerReceivedPictureBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerSentMessageBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerSentPictureBinding;
import hcmute.edu.vn.nhom6.zalo.models.ChatMessage;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Bitmap receiverProfileImg;
    private ArrayList<ChatMessage> chatList;
    private String senderId;

    private int VIEW_TYPE_TEXT_SENT = 1;
    private int VIEW_TYPE_PICTURE_SENT = 2;
    private int VIEW_TYPE_TEXT_RECEIVED = 3;
    private int VIEW_TYPE_PICTURE_RECEIVED = 4;

    public void setReceiverProfileImg(Bitmap bitmap){
        receiverProfileImg = bitmap;
    }

    public ChatAdapter(ArrayList<ChatMessage> chatList,Bitmap receiverProfileImg, String senderId) {
        this.receiverProfileImg = receiverProfileImg;
        this.chatList = chatList;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TEXT_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else if (viewType == VIEW_TYPE_TEXT_RECEIVED) {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else if(viewType == VIEW_TYPE_PICTURE_SENT){
            return new SentPictureViewHolder(
                    ItemContainerSentPictureBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else
            return new ReceivedPictureViewHolder(
                    ItemContainerReceivedPictureBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_TEXT_SENT)
            ((SentMessageViewHolder) holder).setData(chatList.get(position));
        else if(getItemViewType(position) == VIEW_TYPE_PICTURE_SENT)
            ((SentPictureViewHolder) holder).setData(chatList.get(position));
        else if(getItemViewType(position) == VIEW_TYPE_TEXT_RECEIVED)
                ((ReceivedMessageViewHolder) holder).setData(chatList.get(position), receiverProfileImg);
        else
            ((ReceivedPictureViewHolder) holder).setData(chatList.get(position), receiverProfileImg);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatList.get(position);
        if(message.getSenderId().equals(senderId)){
            if(message.getType().equals(Constants.KEY_TEXT_MESSAGE)) return VIEW_TYPE_TEXT_SENT;
            else return VIEW_TYPE_PICTURE_SENT;
        }else
            if(message.getType().equals(Constants.KEY_TEXT_MESSAGE)) return VIEW_TYPE_TEXT_RECEIVED;
            else return VIEW_TYPE_PICTURE_RECEIVED;
    }

    // view cho tin nhắn gửi đi
    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerSentMessageBinding binding;
        public SentMessageViewHolder(ItemContainerSentMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message){
            binding.txtMessage.setText(message.getMessage());
            binding.txtTime.setText(message.getTime());
        }
    }

    // view cho tin nhắn nhận được
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerReceivedMessageBinding binding;
        public ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message, Bitmap receiverProfileImg){
            binding.txtMessage.setText(message.getMessage());
            binding.txtTime.setText(message.getTime());
            if (receiverProfileImg != null)
                binding.imageProfile.setImageBitmap(receiverProfileImg);
        }
    }

    //view cho hình ảnh gửi đi
    static class SentPictureViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerSentPictureBinding binding;
        public SentPictureViewHolder(ItemContainerSentPictureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message){
            binding.imgPicture.setImageBitmap(MyUtilities.decodeImg(message.getMessage()));
            binding.txtTime.setText(message.getTime());
        }
    }

    // view cho hình ảnh nhận được
    static class ReceivedPictureViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerReceivedPictureBinding binding;
        public ReceivedPictureViewHolder(ItemContainerReceivedPictureBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message, Bitmap receiverProfileImg){
            binding.imgPicture.setImageBitmap(MyUtilities.decodeImg(message.getMessage()));
            if (receiverProfileImg != null)
                binding.imageProfile.setImageBitmap(receiverProfileImg);
            binding.txtTime.setText(message.getTime());
        }
    }

}
