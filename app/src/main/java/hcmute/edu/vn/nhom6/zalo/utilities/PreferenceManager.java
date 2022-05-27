package hcmute.edu.vn.nhom6.zalo.utilities;

import android.content.Context;
import android.content.SharedPreferences;

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

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void putSignInInfo(String uid, String phone, String name, String encodedImg, String password){
        putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        putString(Constants.KEY_USER_ID, uid);
        putString(Constants.KEY_PHONE_NUMBER, phone);
        putString(Constants.KEY_NAME, name);
        putString(Constants.KEY_IMAGE, encodedImg);
        putString(Constants.KEY_PASSWORD, password);
    }

}
