package hcmute.edu.vn.nhom6.zalo.adapters;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerReceivedAudioBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerReceivedMessageBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerReceivedPictureBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerSentAudioBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerSentMessageBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerSentPictureBinding;
import hcmute.edu.vn.nhom6.zalo.models.ChatMessage;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Bitmap receiverProfileImg;
    private ArrayList<ChatMessage> chatList;
    private String senderId;
    protected StorageReference storageRef;

    private final int VIEW_TYPE_TEXT_SENT = 1;
    private final int VIEW_TYPE_TEXT_RECEIVED = 2;
    private final int VIEW_TYPE_PICTURE_SENT = 3;
    private final int VIEW_TYPE_PICTURE_RECEIVED = 4;
    private final int VIEW_TYPE_AUDIO_SENT = 5;
    private final int VIEW_TYPE_AUDIO_RECEIVED = 6;

    public void setReceiverProfileImg(Bitmap bitmap){
        receiverProfileImg = bitmap;
    }

    public ChatAdapter(ArrayList<ChatMessage> chatList,Bitmap receiverProfileImg, String senderId, StorageReference storageRef) {
        this.receiverProfileImg = receiverProfileImg;
        this.chatList = chatList;
        this.senderId = senderId;
        this.storageRef = storageRef;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_TEXT_SENT:
                return new SentMessageViewHolder(
                        ItemContainerSentMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_TEXT_RECEIVED:
                return new ReceivedMessageViewHolder(
                        ItemContainerReceivedMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_PICTURE_SENT:
                return new SentPictureViewHolder(
                        ItemContainerSentPictureBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_PICTURE_RECEIVED:
                return new ReceivedPictureViewHolder(
                        ItemContainerReceivedPictureBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_AUDIO_SENT:
                return new SentAudioViewHolder(
                        ItemContainerSentAudioBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_AUDIO_RECEIVED:
                return new ReceivedAudioViewHolder(
                        ItemContainerReceivedAudioBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_TEXT_SENT:
                ((SentMessageViewHolder) holder).setData(chatList.get(position));
                break;
            case VIEW_TYPE_TEXT_RECEIVED:
                ((ReceivedMessageViewHolder) holder).setData(chatList.get(position), receiverProfileImg);
                break;
            case VIEW_TYPE_PICTURE_SENT:
                ((SentPictureViewHolder) holder).setData(chatList.get(position));
                break;
            case VIEW_TYPE_PICTURE_RECEIVED:
                ((ReceivedPictureViewHolder) holder).setData(chatList.get(position), receiverProfileImg);
                break;
            case VIEW_TYPE_AUDIO_SENT:
                ((SentAudioViewHolder) holder).setData(chatList.get(position));
                break;
            case VIEW_TYPE_AUDIO_RECEIVED:
                ((ReceivedAudioViewHolder) holder).setData(chatList.get(position), receiverProfileImg, storageRef);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatList.get(position);
        if(message.getSenderId().equals(senderId)){
            if(message.getType().equals(Constants.KEY_TEXT_MESSAGE))
                return VIEW_TYPE_TEXT_SENT;
            else if(message.getType().equals(Constants.KEY_PICTURE_MESSAGE))
                return VIEW_TYPE_PICTURE_SENT;
            else
                return VIEW_TYPE_AUDIO_SENT;
        }else
            if(message.getType().equals(Constants.KEY_TEXT_MESSAGE))
                return VIEW_TYPE_TEXT_RECEIVED;
            else if (message.getType().equals(Constants.KEY_PICTURE_MESSAGE))
                return VIEW_TYPE_PICTURE_RECEIVED;
            else
                return VIEW_TYPE_AUDIO_RECEIVED;
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

    //view cho audio gửi đi
    static class SentAudioViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerSentAudioBinding binding;
        public SentAudioViewHolder(ItemContainerSentAudioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message){
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());
            if(file.exists()) {
                binding.voicePlayerView.setAudio(file.getAbsolutePath());
                binding.tvNotExist.setVisibility(View.GONE);
                binding.voicePlayerView.setVisibility(View.VISIBLE);
            }else {
                binding.tvNotExist.setVisibility(View.VISIBLE);
                binding.voicePlayerView.setVisibility(View.GONE);
            }
            binding.tvTime.setText(message.getTime());
        }
    }

    // view cho audio nhận được
    static class ReceivedAudioViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerReceivedAudioBinding binding;

        public ReceivedAudioViewHolder(ItemContainerReceivedAudioBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message, Bitmap receiverProfileImg, StorageReference storageRef){
            if (receiverProfileImg != null)
                binding.imageProfile.setImageBitmap(receiverProfileImg);

//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());

            StorageReference audioRef = storageRef.child(Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());
            audioRef.getDownloadUrl().addOnSuccessListener( uri -> {
                binding.voicePlayerView.setAudio(uri.toString());
                binding.tvNotExist.setVisibility(View.GONE);
                binding.voicePlayerView.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> {
                MyUtilities.showToast(binding.getRoot().getContext(), "Lỗi khi lấy audio từ firebase");
                binding.tvNotExist.setVisibility(View.VISIBLE);
                binding.voicePlayerView.setVisibility(View.GONE);
            });

            binding.tvTime.setText(message.getTime());
        }

    }

}
