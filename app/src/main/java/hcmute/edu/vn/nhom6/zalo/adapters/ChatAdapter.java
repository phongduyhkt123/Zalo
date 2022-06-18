package hcmute.edu.vn.nhom6.zalo.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
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
import hcmute.edu.vn.nhom6.zalo.utilities.DownloadFile;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

/** adapter hiển thị lịch sử tin nhắn */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Bitmap receiverProfileImg; // hình ảnh của người đang chat với
    private ArrayList<ChatMessage> chatList; // danh sách tin nhắn
    private String senderId; // người dùng hiện tại
    protected StorageReference storageRef; // storage để lấy file trên firebase storage

    private final int VIEW_TYPE_TEXT_SENT = 1;
    private final int VIEW_TYPE_TEXT_RECEIVED = 2;
    private final int VIEW_TYPE_PICTURE_SENT = 3;
    private final int VIEW_TYPE_PICTURE_RECEIVED = 4;
    private final int VIEW_TYPE_AUDIO_SENT = 5;
    private final int VIEW_TYPE_AUDIO_RECEIVED = 6;

    /** thiết lập ảnh cho người đang chat với */
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
            case VIEW_TYPE_TEXT_SENT: // trường hợp tin nhắn văn bản gửi đi
                return new SentMessageViewHolder(
                        ItemContainerSentMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_TEXT_RECEIVED: // trường hợp tin nhắn văn bản nhận được
                return new ReceivedMessageViewHolder(
                        ItemContainerReceivedMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_PICTURE_SENT: // trường hợp tin nhắn hình ảnh gửi đi
                return new SentPictureViewHolder(
                        ItemContainerSentPictureBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_PICTURE_RECEIVED: // trường hợp tin nhắn hình ảnh nhận được
                return new ReceivedPictureViewHolder(
                        ItemContainerReceivedPictureBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_AUDIO_SENT: // trường hợp tin nhắn audio gửi đi
                return new SentAudioViewHolder(
                        ItemContainerSentAudioBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_AUDIO_RECEIVED: // trường hợp tin nhắn audio nhận được
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
                ((SentPictureViewHolder) holder).setData(chatList.get(position), storageRef);
                break;
            case VIEW_TYPE_PICTURE_RECEIVED:
                ((ReceivedPictureViewHolder) holder).setData(chatList.get(position), receiverProfileImg, storageRef);
                break;
            case VIEW_TYPE_AUDIO_SENT:
                ((SentAudioViewHolder) holder).setData(chatList.get(position), storageRef);
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

    /** thực hiện lấy loại view từ loại tin nhắn */
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
            binding.txtMessage.setText(message.getMessage()); // đặt nội dung cho text view tin nhắn
            binding.txtTime.setText(message.getTime()); // đặt nội dung cho text view thời gian
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
            binding.txtMessage.setText(message.getMessage()); // đặt nội dung cho text view tin nhắn
            binding.txtTime.setText(message.getTime()); // đặt nội dung cho text view thời gian
            if (receiverProfileImg != null)
                binding.imageProfile.setImageBitmap(receiverProfileImg); // đặt hình ảnh người đang chat với
        }
    }

    //view cho hình ảnh gửi đi
    static class SentPictureViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerSentPictureBinding binding;
        public SentPictureViewHolder(ItemContainerSentPictureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message, StorageReference storageRef){
            if(message.isStoredSender()){ // hình ảnh chưa bị tự động xóa
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        Constants.KEY_IMAGE_PATH + File.separator + message.getMessage());

                if(file.exists()) { // đã tải ảnh về máy thì  load ảnh lên
                    binding.imgPicture.setImageBitmap(
                            BitmapFactory.decodeFile(file.getAbsolutePath()) // lấy hình ảnh từ đường dẫn
                    );
                }else { // chưa tải ảnh về mày thì tiến hành tải ảnh
                    // Lấy hình ảnh từ firebase
                    StorageReference audioRef = storageRef.child(Constants.KEY_IMAGE_PATH + File.separator + message.getMessage());
                    audioRef.getDownloadUrl().addOnSuccessListener( uri -> {
                        try {
                            // Dùng thư viện Picasso để load hình ảnh
                            Picasso.get().load(uri.toString()).into(binding.imgPicture);
                            // Lưu hình ảnh về máy
                            MyUtilities.saveImage(
                                    ((BitmapDrawable) (binding.imgPicture.getDrawable())).getBitmap(),
                                    message.getMessage()
                            );

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).addOnFailureListener(e -> {
                        MyUtilities.showToast(binding.getRoot().getContext(), "Lỗi khi lấy ảnh từ firebase");
                    });
                }
                binding.tvNotExist.setVisibility(View.GONE);
                binding.imgPicture.setVisibility(View.VISIBLE);
            }else{ // hình ảnh đã bị tự động xóa
                // Hiện thông báo hình ảnh bị xóa
                binding.tvNotExist.setVisibility(View.VISIBLE);
                binding.imgPicture.setVisibility(View.GONE);
            }

            binding.txtTime.setText(message.getTime()); // hiện thời gian
        }
    }

    // view cho hình ảnh nhận được
    static class ReceivedPictureViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerReceivedPictureBinding binding;
        public ReceivedPictureViewHolder(ItemContainerReceivedPictureBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message, Bitmap receiverProfileImg, StorageReference storageRef){
            if(message.isStoredReceiver()){ // hình ảnh chưa bị tự động xóa
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        Constants.KEY_IMAGE_PATH + File.separator + message.getMessage());

                if(file.exists()) { // đã tải ảnh về máy thì  load ảnh lên
                    binding.imgPicture.setImageBitmap(
                            BitmapFactory.decodeFile(file.getAbsolutePath()) // lấy hình ảnh từ đường dẫn
                    );
                }else { // chưa tải ảnh về mày thì tiến hành tải ảnh
                    // Lấy hình ảnh từ firebase
                    StorageReference audioRef = storageRef.child(Constants.KEY_IMAGE_PATH + File.separator + message.getMessage());
                    audioRef.getDownloadUrl().addOnSuccessListener( uri -> {
                        try {
                            // Dùng thư viện Picasso để load hình ảnh
                            Picasso.get().load(uri.toString()).into(binding.imgPicture);
                            // Lưu hình ảnh về máy
                            MyUtilities.saveImage(
                                    ((BitmapDrawable) (binding.imgPicture.getDrawable())).getBitmap(),
                                    message.getMessage()
                            );

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).addOnFailureListener(e -> {
                        MyUtilities.showToast(binding.getRoot().getContext(), "Lỗi khi lấy ảnh từ firebase");
                    });
                }
                binding.tvNotExist.setVisibility(View.GONE);
                binding.imgPicture.setVisibility(View.VISIBLE);
            }else{ // hình ảnh đã bị tự động xóa
                // Hiện thông báo hình ảnh bị xóa
                binding.tvNotExist.setVisibility(View.VISIBLE);
                binding.imgPicture.setVisibility(View.GONE);
            }

            if (receiverProfileImg != null)
                binding.imageProfile.setImageBitmap(receiverProfileImg); // hiện ảnh người gửi

            binding.txtTime.setText(message.getTime()); // hiện thời gian
        }


    }

    //view cho audio gửi đi
    static class SentAudioViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerSentAudioBinding binding;
        public SentAudioViewHolder(ItemContainerSentAudioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(ChatMessage message, StorageReference storageRef){
            if (message.isStoredSender()){ // audio chưa bị tự động xóa
                //Lấy audio từ thiết bị
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());
                if(file.exists()) { // đã tải về máy thì load lên
                    binding.voicePlayerView.setAudio(file.getAbsolutePath());

                }else { // chưa tải về thì tải về
                    StorageReference audioRef = storageRef.child(Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());
                    audioRef.getDownloadUrl().addOnSuccessListener( uri -> {
                        binding.voicePlayerView.setAudio(uri.toString()); // load audio
                        saveAudio(uri.toString(), message.getMessage()); // lưu audio
                    }).addOnFailureListener(e -> {
                        MyUtilities.showToast(binding.getRoot().getContext(), "Lỗi khi lấy audio từ firebase");
                    });
                }
                binding.tvNotExist.setVisibility(View.GONE);
                binding.voicePlayerView.setVisibility(View.VISIBLE);
            } else { // audio đã bị tự động xóa
                binding.tvNotExist.setVisibility(View.VISIBLE);
                binding.voicePlayerView.setVisibility(View.GONE);
            }

            binding.tvTime.setText(message.getTime());
        }

        /** tải audio về máy **/
        private void saveAudio(String url, String fileName) {
            DownloadFile downloadFile = new DownloadFile(fileName);
            downloadFile.execute(url);
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
            if (message.isStoredReceiver()){ // audio chưa bị tự động xóa
                //Lấy audio từ thiết bị
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());
                if(file.exists()) { // đã tải về máy thì load lên
                    binding.voicePlayerView.setAudio(file.getAbsolutePath());

                }else { // chưa tải về thì tải về
                    StorageReference audioRef = storageRef.child(Constants.KEY_AUDIO_PATH + File.separator + message.getMessage());

                    audioRef.getDownloadUrl().addOnCompleteListener( task -> {
                        binding.voicePlayerView.setAudio(task.getResult().toString()); // load audio
                        saveAudio(task.getResult().toString(), message.getMessage()); // lưu audio
                    }).addOnFailureListener(e -> {
//                        MyUtilities.showToast(binding.getRoot().getContext(), "Lỗi khi lấy audio từ firebase");
                        e.printStackTrace();
                    });
                }
                binding.tvNotExist.setVisibility(View.GONE);
                binding.voicePlayerView.setVisibility(View.VISIBLE);
            } else { // audio đã bị tự động xóa
                binding.tvNotExist.setVisibility(View.VISIBLE);
                binding.voicePlayerView.setVisibility(View.GONE);
            }

            if (receiverProfileImg != null)
                binding.imageProfile.setImageBitmap(receiverProfileImg);
            binding.tvTime.setText(message.getTime());
        }

        private void saveAudio(String url, String fileName) {
//            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI); // chỉ cho phép download qua wifi
//            request.setVisibleInDownloadsUi(false);
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//            String filePath = Environment.getExternalStorageDirectory() + File.separator + Constants.KEY_AUDIO_PATH + File.separator + fileName;
//            request.setDestinationInExternalFilesDir(binding.getRoot().getContext(), null, filePath);
//
//            DownloadManager downloadManager = (DownloadManager) binding.getRoot().getContext().getSystemService(Context.DOWNLOAD_SERVICE);
//            if(downloadManager != null){
//                downloadManager.enqueue(request);
//            }

            DownloadFile downloadFile = new DownloadFile(fileName);
            downloadFile.execute(url); // doInBackground
        }
    }

}
