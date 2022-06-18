package hcmute.edu.vn.nhom6.zalo.listeners;

import hcmute.edu.vn.nhom6.zalo.models.User;

/** Listener lắng nghe sự kiện ở mỗi dòng liên hệ */
public interface UserListener {
    void onUserClicked(User user); // thực hiện công việc khi dòng liên hệ được nhấn
}
