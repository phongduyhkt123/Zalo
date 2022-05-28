package hcmute.edu.vn.nhom6.zalo.activities.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.listeners.UserListener;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;

public class RowContactAdapter extends RecyclerView.Adapter<RowContactAdapter.MyViewHolder> implements Filterable {

    private ArrayList<User> contactList;
    private ArrayList<User> contactListOld;
    private UserListener userListener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                if(query.isEmpty()){
                    contactList = contactListOld;
                }else{
                    ArrayList<User> tempList = new ArrayList<>();
                    for (User user: contactListOld) {
                        if(user.getName().toLowerCase().contains(query.toLowerCase()) || user.getPhoneNumber().contains(query)){
                            tempList.add(user);
                        }
                    }
                    contactList = tempList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    contactList = (ArrayList<User>) results.values;
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        };
    }

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
        this.contactListOld = contactList;
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

    public void setDataList(ArrayList<User> list){
        this.contactListOld = list;
    }
}
