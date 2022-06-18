package hcmute.edu.vn.nhom6.zalo.activities.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.activities.BaseActivity;
import hcmute.edu.vn.nhom6.zalo.databinding.DeleteCycleBinding;
import hcmute.edu.vn.nhom6.zalo.utilities.Constants;
import hcmute.edu.vn.nhom6.zalo.utilities.MyUtilities;
import hcmute.edu.vn.nhom6.zalo.utilities.PreferenceManager;

/** thiết lập thời gian tự động xóa file */
public class DeleteCycleActivity
        extends BaseActivity
        implements AdapterView.OnItemSelectedListener /* Để làm listener cho spinner */ {

    DeleteCycleBinding binding;
    PreferenceManager preferenceManager; // sharedPreference
    FirebaseFirestore db; // csdl
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeleteCycleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        binding.topAppBar.setNavigationOnClickListener(t -> {finish();});
        preferenceManager = new PreferenceManager(getApplicationContext());
        setUpSpinner();
        setListeners();
    }

    /** thiết lập cho spinner */
    private void setUpSpinner(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.life_period, // list thời gian
                android.R.layout.simple_spinner_item // layout cho item của spinner
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.spnTime.setAdapter(adapter);

        List<String> periodList = Arrays.asList((getResources().getStringArray(R.array.life_period)));

        if(preferenceManager.getInt(Constants.KEY_DELETE_PERIOD) == -1) { // chưa thiết lập tự động xóa
            binding.spnTime.setSelection(periodList.size() - 1); // hiện giá trị "Không bao giờ"
        }else{
            int period = preferenceManager.getInt(Constants.KEY_DELETE_PERIOD);
            switch (period){
                case 7: { // 1 tuần
                    binding.spnTime.setSelection(0);
                    break;
                }
                case 30: { // 1 tháng
                    binding.spnTime.setSelection(1);
                    break;
                }
                case 120: { // 4 tháng
                    binding.spnTime.setSelection(2);
                    break;
                }
            }
        }
    }

    private void setListeners(){
        binding.txtSubmit.setOnClickListener(v -> {
            final List<Integer> periodList = Arrays.asList(7, 30, 120, -1);
            // cập nhật lên Firebase
            db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).update(
                    Constants.KEY_DELETE_PERIOD,
                    periodList.get(binding.spnTime.getSelectedItemPosition())
            );
            // cập nhật sharedPreference
            preferenceManager.putInt(Constants.KEY_DELETE_PERIOD, periodList.get(binding.spnTime.getSelectedItemPosition()));
            MyUtilities.showToast(getApplicationContext(), "Cập nhật thành công");
        });
    }

    //Tạm thời chưa dùng đến 2 hàm này
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}