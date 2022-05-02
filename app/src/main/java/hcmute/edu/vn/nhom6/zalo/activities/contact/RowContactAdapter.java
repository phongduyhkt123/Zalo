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
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

public class RowContactAdapter extends RecyclerView.Adapter<RowContactAdapter.MyViewHolder> {

    private ArrayList<User> contactList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt;
        TextView phone_txt;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.txt_name);
            phone_txt = itemView.findViewById(R.id.txt_phone);
            imageView = itemView.findViewById(R.id.iv_image);
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

    }

    public RowContactAdapter(ArrayList<User> contactList){
        this.contactList = contactList;
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
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
