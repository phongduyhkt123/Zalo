package hcmute.edu.vn.nhom6.zalo.adapters;

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

/** adapter để hiện từng dòng liên hệ
 * dùng ở fragment danh bạ và ở activity tìm kiếm người dùng*/
public class RowContactAdapter extends RecyclerView.Adapter<RowContactAdapter.MyViewHolder> implements Filterable  /* dùn để lọc người dùng cho trang tìm kiếm*/{

    private ArrayList<User> contactList; // danh sách user sau khi được filter (ở trang tìm kiếm)
    private ArrayList<User> contactListOld; // danh sách người dùng truyền vào lúc đầu
    private final UserListener userListener; // Listener lắng nghe sự kiện ở mỗi dòng liên hệ

    /** Hàm thực hiện lọc liên hệ */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString(); // key word để lọc
                if(query.isEmpty()){ // nếu không có key word thì danh sách sau khi lọc bằng danh sách lúc đầu truyền vào
                    contactList = contactListOld;
                }else{ // có lọc
                    ArrayList<User> tempList = new ArrayList<>(); // tạo một list tạm
                    /* xét từng user trong list ban đầu
                    nếu có tên hoặc số điện thoại giống với key word thì thêm vào list tạm */
                    for (User user: contactListOld) {
                        String addHeadPhone = MyUtilities.formatPhoneAddHead(user.getPhoneNumber());
                        String deHeadPhone = MyUtilities.formatPhoneDeHead(user.getPhoneNumber());
                        if(user.getName().toLowerCase().contains(query.toLowerCase()) ||
                                addHeadPhone.equals(query) ||
                                deHeadPhone.equals(query)){
                            tempList.add(user);
                        }
                    }
                    contactList = tempList; // gán list tạm cho list lọc
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults; // trả về kết quả lọc
            }

            /** Hàm thược hiện lấy kết quả lọc vào contactList và gọi notify để cập nhật danh sách liên hệ */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    contactList = (ArrayList<User>) results.values;
                    notifyDataSetChanged();
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
            }
        };
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt; // text view hiện tên của user
        TextView phone_txt; // text view hiện số điện thoại của user
        ImageView imageView; // image view hiện ảnh đại diện
        ViewGroup container; // layout dòng liên hệ
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // ánh xạ các thành phần
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
        holder.getName_txt().setText(contactList.get(position).getName()); // set text cho text view tên
        holder.getPhone_txt().setText(contactList.get(position).getPhoneNumber()); // set text cho text view số điện thoại
        String encodedImg = contactList.get(position).getImage(); // set hình ảnh cho image view
        if(encodedImg != null)
            holder.getImageView().setImageBitmap(MyUtilities.decodeImg(encodedImg));

        holder.getContainer().setOnClickListener(v -> userListener.onUserClicked(contactList.get(position))); // đặt listener
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void setDataList(ArrayList<User> list){
        this.contactListOld = list;
    }
}
