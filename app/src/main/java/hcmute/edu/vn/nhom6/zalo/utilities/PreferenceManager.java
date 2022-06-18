package hcmute.edu.vn.nhom6.zalo.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/** Lớp xử lý các giá trị sharedPreference*/
public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /** đưa thông tin đăng nhập lên sharedPreference */
    public void putSignInInfo(String uid, String phone, String name, String encodedImg, String password, long deletePeriod){
        putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        putString(Constants.KEY_USER_ID, uid);
        putString(Constants.KEY_PHONE_NUMBER, phone);
        putString(Constants.KEY_NAME, name);
        putString(Constants.KEY_IMAGE, encodedImg);
        putString(Constants.KEY_PASSWORD, password);
        putInt(Constants.KEY_DELETE_PERIOD, (int)deletePeriod);
    }

    /** ghi nhớ đăng nhập */
    public void putRememberSignIn(String phone, String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_REMEMBER_PHONE, phone);
        editor.putString(Constants.KEY_REMEMBER_PASSWORD, password);
        editor.commit();
    }

    /** xóa ghi nhớ đăng nhập */
    public void clearRememberSignIn(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.KEY_REMEMBER_PHONE);
        editor.remove(Constants.KEY_REMEMBER_PASSWORD);
        editor.commit();
    }

}
