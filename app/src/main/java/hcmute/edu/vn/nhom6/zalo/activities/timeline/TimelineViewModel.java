package hcmute.edu.vn.nhom6.zalo.activities.timeline;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimelineViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TimelineViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is timeline fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}