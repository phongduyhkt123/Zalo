package hcmute.edu.vn.nhom6.zalo.activities.contact;

import static android.Manifest.permission.READ_CONTACTS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.nhom6.zalo.activities.BaseFragment;
import hcmute.edu.vn.nhom6.zalo.activities.mess.ChatActivity;
import hcmute.edu.vn.nhom6.zalo.adapters.GroupAdapter;
import hcmute.edu.vn.nhom6.zalo.databinding.FragmentContactBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.UserListener;
import hcmute.edu.vn.nhom6.zalo.models.User;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** Fragment hiển thị danh sách liên hệ */
public class ContactFragment extends BaseFragment /*with user availability*/ implements UserListener /* interface lắng nghe sự kiện ở dòng liên hệ*/ {
    private boolean readContactPermission = false; // cho biết có được phép đọc danh bạ không
    private FragmentContactBinding binding;
    private PreferenceManager preferenceManager; // sharedPreference
    private Map<String, ArrayList<User>> groupList = new HashMap<>(); // danh sách liên hệ và nhóm liên hệ
    private GroupAdapter groupAdapter; // adapter cho nhóm liên hệ

    // Tạo ActivityResultLauncher để thực hiện yêu cầu cấp phép đọc danh bạ
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    //Permission granted
                    readContactPermission = true;
                } else {
                    //permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_CONTACTS)) {
                        // Có thể thêm một số thuyết phục để người dùng cho phép quyền truy cập
                        MyUtilities.showToast(getActivity(), "Hãy cho phép truy cập danh bạ!");

                    } else {
                        //display error dialog
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ContactViewModel dashboardViewModel =
        new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        setData();

        // Tạo adapter hiển thị nhóm liên hệ và các liên hệ trong nhóm
        groupAdapter = new GroupAdapter(container.getContext(), groupList, this);
        binding.rv1.setAdapter(groupAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rv1.setLayoutManager(layoutManager);

        setListeners();

        /* Kiểm tra xem có quyền đọc danh dạ không
        * Nếu không có thì tiến hành yêu cầu cấp quyền
        * Ngược lại thì cập nhật giá trị của biến readContactPermission */
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){ // chưa có quyền
            MyUtilities.showToast(getActivity(), "no contacts permission!");
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }else{ // có quyền
            readContactPermission = true;
//            MyUtilities.showToast(getActivity(), "have permission");
        }

        return root;
    }

    private void setData(){
        ArrayList<User> mContactList = new ArrayList<>(); // danh sách các liên hệ của ứng dụng
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // csdl
        // lấy danh sách các user trong csdl
        db.collection(Constants.KEY_COLLECTION_USERS).get()
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()
                    && task.getResult() != null
                    && task.getResult().getDocuments().size() > 0){
                for(QueryDocumentSnapshot i: task.getResult()) {
                    //bỏ qua số điện thoại của user hiện tại
                    if(i.getString(Constants.KEY_PHONE_NUMBER).equals(preferenceManager.getString(Constants.KEY_PHONE_NUMBER))){
                        continue;
                    }
                    mContactList.add(new User(
                            i.getId(),
                            i.getString(Constants.KEY_NAME),
                            i.getString(Constants.KEY_PHONE_NUMBER),
                            i.getString(Constants.KEY_FCM_TOKEN),
                            i.getString(Constants.KEY_IMAGE)
                    ));
                }
                groupList.put(Constants.KEY_ALL_CONTACT, mContactList); // đưa vào nhóm tất cả liên hệ

                if( readContactPermission ){ // nếu được phép đọc danh bạ thì thực hiện đồng bộ hóa
                    // gọi hàm lấy danh sách liên hệ đồng bộ
                    ArrayList<User> deviceContactList = getContactListSync(/*truyền list vào để kiểm tra số điện thoại*/
                            mContactList
                    ); // để ở đây vì khi lấy được dữ liệu từ firebase thì mới kiểm tra được số điện thoại
                    groupList.put(Constants.KEY_SYNC_DEVICE_CONTACT, deviceContactList); // đưa vào nhóm bạn trong danh bạ
                }

                groupAdapter.notifyDataSetChanged(); // notify cập nhật dữ liệu
            }
        }).addOnFailureListener(e -> {
            MyUtilities.showToast(getContext(), "Không thể tải danh bạ: "+ e.getMessage());
            e.printStackTrace();
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setListeners(){

    }

    /** Hàm xử lý khi một dòng liên hệ được nhấn
     * được dùng ở RowAdapter
     * Thực hiện mở trang chat giữa người dùng hiện tại và người dùng trong liên hệ được nhấn */
    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user); // truyền user trong liên hệ cho intent
        startActivity(intent);
    }

    /** Hàm thực hiện lấy danh sách người dùng có đăng ký tài khoản ứng dụng trong danh bạ
     * để đồng bộ hóa danh dạ */
    @SuppressLint("Range")
    private ArrayList<User> getContactListSync(ArrayList<User> mContactList /* danh sách tất cả người dùng của ứng dụng */) {
        // ContentResolver giống như một nơi để chia sẽ dữ liệu giữa các ứng dụng
        ContentResolver cr = getActivity().getContentResolver();
        // Lấy tất cả thông tin danh bạ ở bảng contacts ( bảng này không có số điện thoại người dùng )
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        ArrayList<User> deviceContactList = new ArrayList<>(); // tạo list để lưu thông tin của người dùng đồng bộ
        if ((cur != null ? cur.getCount() : 0) > 0) {
            /* duyệt qua tất cả người dùng trong danh bạ
            * Nếu có số điện thoại trùng với số diện thoại người dùng của ứng dụng thì thêm vào danh sách */
            while (cur != null && cur.moveToNext()) {

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)); // lấy id của người dùng để truy vấn ở bảng data lấy số điện thoại
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)); // lấy tên của người dùng

                // Để lấy được số điện thoại thì phải truy vấn ở bảng data (ở trên là bảng contact)
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) { // Nếu người này có số điện thoại thì
                    // truy vấn bảng data
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", // điều kiện là id bằng với id ở trên
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));  // lấy số điện thoại của người dùng
                        // xét nếu sđt trùng thì thêm vào danh sách đồng bộ
                        for (User i : mContactList) {
                            String phone = i.getPhoneNumber().replace("+84", "0");
                            // nếu số điện thoại này có trong ứng dụng thì lấy
                            if(phoneNo.equals(phone)){
                                User user = new User();
                                user.setId(i.getId());
                                user.setName(name);
                                user.setPhoneNumber(phone);
                                deviceContactList.add(user);
                            }
                        }

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return deviceContactList; // trả về danh sách đồng bộ
    }
}