package hcmute.edu.vn.nhom6.zalo.listeners;

import hcmute.edu.vn.nhom6.zalo.models.User;

/** interface thực hiện xử lý với conversation ở fragment message -- recentMessageAdapter */
public interface ConversionListener {
    void onConversionClicked(User user); // xử lý khi conversation được nhấn
}
