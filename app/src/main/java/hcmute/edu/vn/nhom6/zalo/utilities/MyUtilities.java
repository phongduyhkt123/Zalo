package hcmute.edu.vn.nhom6.zalo.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MyUtilities {
    public static void showToast(Context context, String mess){
        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
    }

    // Chuyển bitmap sang string encode base64
    public static String encodeImg(Bitmap bitmap, int width){
        int previewWidth = width;
        int previewHeight = bitmap.getHeight() * previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // Chuyển string encode base64 sang bitmap
    public static Bitmap decodeImg(String stringByteArray){
        try {
            byte[] byteArray = Base64.decode(stringByteArray, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String randomString(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int len = 70;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static String getStringDate(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    public static String formatPhoneAddHead(String phone){
        if(Character.compare('0', phone.charAt(0)) == 0)
            phone = phone.replaceFirst("0", "+84");
        return phone;
    }

    public static String formatPhoneDeHead(String phone){
        if(phone.contains("+84"))
            phone = phone.replace("+84", "0");
        return phone;
    }

    public static void saveImage(Bitmap bitmap, String fileName){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_IMAGE_PATH);

        if(!file.exists()){
            file.mkdirs();
        }

        String imagePath = file.getAbsolutePath() + File.separator + fileName; // đường dẫn hình ảnh

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // nén bitmap vào file OutputStream
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
