package hcmute.edu.vn.nhom6.zalo.activities.mess;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MessViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is messages fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}