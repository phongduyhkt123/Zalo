package hcmute.edu.vn.nhom6.zalo.activities.contact;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.adapters.UsersAdapter;
import hcmute.edu.vn.nhom6.zalo.databinding.ItemContainerUserBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.UserListener;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

public class RowContactAdapter extends RecyclerView.Adapter<RowContactAdapter.MyViewHolder> {

    private ArrayList<User> contactList;
    private UserListener userListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt;
        TextView phone_txt;
        ImageView imageView;
        ViewGroup container;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.txt_name);
            phone_txt = itemView.findViewById(R.id.txt_phone);
            imageView = itemView.findViewById(R.id.iv_image);
            container = itemView.findViewById(R.id.layout_row_contact);
        }

        public TextView getPhone_txt() {
            return phone_txt;
        }

        public TextView getName_txt() {
            return name_txt;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ViewGroup getContainer() {
            return container;
        }

    }

    public RowContactAdapter(ArrayList<User> contactList, UserListener userListener){
        this.contactList = contactList;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_contact, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.getName_txt().setText(contactList.get(position).getName());
        holder.getPhone_txt().setText(contactList.get(position).getPhoneNumber());
        String encodedImg = contactList.get(position).getImage();
        if(encodedImg != null)
            holder.getImageView().setImageBitmap(MyUtilities.decodeImg(encodedImg));

        holder.getContainer().setOnClickListener(v -> userListener.onUserClicked(contactList.get(position)));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
