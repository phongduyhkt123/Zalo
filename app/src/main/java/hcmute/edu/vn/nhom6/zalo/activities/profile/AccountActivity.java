package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.InputStream;

import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.AccountSittingBinding;
import hcmute.edu.vn.nhom6.zalo.databinding.DialogChangeNameBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** Activity quản lý tài khoản */
public class AccountActivity extends BaseActivity {
    private AccountSittingBinding binding;
    private PreferenceManager preferenceManager; // sharedPreference
    private String encodedImg; // lưu hình ảnh tạm thời
    private FirebaseFirestore db; // csdl

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccountSittingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        setData();
        setListeners();
    }

    private void setData() {
        // Điền thông tin của người dùng hiện tại
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            binding.ivAvt.setImageBitmap(
                    MyUtilities.decodeImg(preferenceManager.getString(Constants.KEY_IMAGE)));
            binding.txtName.setText(preferenceManager.getString(Constants.KEY_NAME));
            binding.txtPhone.setText(preferenceManager.getString(Constants.KEY_PHONE_NUMBER));
        }
    }

    private void setListeners() {
        binding.layoutPhone.setOnClickListener(t -> {
            Intent intent = new Intent(AccountActivity.this, ChangePhoneActivity.class );
            startActivity(intent);
        });

        binding.txtChangePassword.setOnClickListener(t -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class );
            startActivity(intent);
        });

        binding.topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // Muốn đổi ảnh đại diện
        binding.ivAvt.setOnClickListener(t -> {
            // mở intent chọn ảnh từ thư viện
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });

        // Lưu ảnh đại diện
        binding.buttonSave.setOnClickListener(t -> {
            onUpdateImgClicked();
        });

        // Hủy thay đổi ảnh đại diện
        binding.buttonCancel.setOnClickListener(t -> {
            // Đặt lại hình ảnh ban đầu
            binding.ivAvt.setImageBitmap(MyUtilities.decodeImg(preferenceManager.getString(Constants.KEY_IMAGE)));
            binding.buttonCancel.setVisibility(View.GONE);
            binding.buttonSave.setVisibility(View.GONE);
        });

        binding.txtChangeName.setOnClickListener( v -> {
            openChangeNameDialog(); // mở dialog thay đổi tên
        });
    }

    /** Mở dialog để thay đổi tên */
    private void openChangeNameDialog() {
        DialogChangeNameBinding dBinding = DialogChangeNameBinding.inflate(getLayoutInflater());
        ChangeNameDialog dialog = new ChangeNameDialog(dBinding);
        dBinding.inputName.setText(preferenceManager.getString(Constants.KEY_NAME));
        dBinding.buttonCancel.setOnClickListener( v -> dialog.dismiss());
        // chọn lưu
        dBinding.buttonSave.setOnClickListener( v -> {
            String name = dBinding.inputName.getText().toString().trim();
            if(name.isEmpty() || name.equals(preferenceManager.getString(Constants.KEY_NAME))) {
                MyUtilities.showToast(getApplicationContext(), "Vui lòng nhập tên!");
                return;
            }
            // cập nhật tên lên csdl
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .update(Constants.KEY_NAME, name)
                    .addOnSuccessListener(result -> {
                        MyUtilities.showToast(getApplicationContext(), "Đổi tên thành công!");
                        binding.txtName.setText(name);
                        preferenceManager.putString(Constants.KEY_NAME, name); // cập nhật tên trong sharedPreference
                        updateNameInConversion(); // cập nhật tên trong bảng conversation
                        dialog.dismiss();
                    })
                    .addOnFailureListener( e -> {
                        e.printStackTrace();
                        MyUtilities.showToast(getApplicationContext(), "Có lỗi xảy ra!");
                    });
        });

        dialog.show(getSupportFragmentManager(), "dialog");
    }
    /** Đổi tên trong bảng conversation */
    private void updateNameInConversion() {
        /*cập nhật tên trong conversation*/
        // current user là sender
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    updateName(task, Constants.KEY_SENDER);
                });
        // current user là receiver
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    updateName(task, Constants.KEY_RECEIVER);
                });
    }

    /** hàm đổi ảnh đại diện */
    private void onUpdateImgClicked() {
        // cần đổi ảnh trong firebase và cả trong conversations
        // đầu tiên đổi trong firebase
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(Constants.KEY_IMAGE, encodedImg) // cập nhật img trên db
                .addOnSuccessListener(v -> {

                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImg); // cập nhật img lưu trên preferenceManager
                    // Ẩn lưu hủy
                    binding.buttonSave.setVisibility(View.GONE);
                    binding.buttonCancel.setVisibility(View.GONE);
                    MyUtilities.showToast(getApplicationContext(), "Đã cập nhật ảnh đại diện");

                    /*cập nhật hình ảnh trong conversation*/
                    // current user là receiver
                    db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                            .get()
                            .addOnCompleteListener(task -> {
                                updateImg(task, Constants.KEY_RECEIVER);
                            });

                    // current user là sender
                    db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                            .get()
                            .addOnCompleteListener(task -> {
                                updateImg(task, Constants.KEY_SENDER);
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    MyUtilities.showToast(getApplicationContext(), "Có lỗi xảy ra!");
                });

    }

    /** đổi ảnh đại diện ở bảng conversation */
    private void updateImg(Task<QuerySnapshot> task, String userRole) {
        if(task.isSuccessful()
                && task.getResult() != null
                && task.getResult().getDocuments().size() >0){
            for(QueryDocumentSnapshot i : task.getResult()) {
                db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(i.getId())
                        .update(
                          userRole.equals(Constants.KEY_SENDER)?
                                  Constants.KEY_SENDER_IMAGE:
                                  Constants.KEY_RECEIVER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE)
                        );
            }
        }
    }
    /** đổi tên ở bảng conversation */
    private void updateName(Task<QuerySnapshot> task, String userRole) {
        if(task.isSuccessful()
                && task.getResult() != null
                && task.getResult().getDocuments().size() >0){
            for(QueryDocumentSnapshot i : task.getResult()) {
                db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(i.getId())
                        .update(
                                userRole.equals(Constants.KEY_SENDER)?
                                        Constants.KEY_SENDER_NAME:
                                        Constants.KEY_RECEIVER_NAME, preferenceManager.getString(Constants.KEY_NAME)
                        );
            }
        }
    }

    /** cho người dùng chọn hình ảnh từ thư viện */
    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream); //chuyển inputstream sang bitmap
                            binding.ivAvt.setImageBitmap(bitmap); // đưa hình ảnh lên imageview
                            encodedImg = MyUtilities.encodeImg(bitmap, 200); // encode bitmap sang string
                            // Hiện nút lưu hủy
                            binding.buttonSave.setVisibility(View.VISIBLE);
                            binding.buttonCancel.setVisibility(View.VISIBLE);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    /** Dialog đổi tên */
    public static class ChangeNameDialog extends DialogFragment{
        private DialogChangeNameBinding binding;
        public ChangeNameDialog(DialogChangeNameBinding binding){
            this.binding = binding;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(binding.getRoot());

            return builder.create();
        }
    }
}