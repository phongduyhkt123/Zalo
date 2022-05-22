package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.AccountSittingBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

public class AccountActivity extends BaseActivity {
    private AccountSittingBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccountSittingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        preferenceManager = new PreferenceManager(getApplicationContext());
        setData();
        setListeners();
    }

    private void setData() {
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

        binding.back.setOnClickListener(t -> { finish(); });

        binding.ivAvt.setOnClickListener(t -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });

        binding.buttonSave.setOnClickListener(t -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .update(Constants.KEY_IMAGE, encodedImg) // cập nhật img trên db
                    .addOnSuccessListener(v -> {
                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImg); // cập nhật img lưu trên preferenceManager
                        // Ẩn lưu hủy
                        binding.buttonSave.setVisibility(View.GONE);
                        binding.buttonCancel.setVisibility(View.GONE);
                        MyUtilities.showToast(getApplicationContext(), "Đã cập nhật ảnh đại diện");
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        MyUtilities.showToast(getApplicationContext(), "Có lỗi xảy ra!");
                    });
        });

        binding.buttonCancel.setOnClickListener(t -> {
            // Đặt lại hình ảnh ban đầu
            binding.ivAvt.setImageBitmap(MyUtilities.decodeImg(preferenceManager.getString(Constants.KEY_IMAGE)));
            binding.buttonCancel.setVisibility(View.GONE);
            binding.buttonSave.setVisibility(View.GONE);
        });
    }

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
}