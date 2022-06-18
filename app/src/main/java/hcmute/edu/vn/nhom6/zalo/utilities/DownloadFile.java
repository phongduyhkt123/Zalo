package hcmute.edu.vn.nhom6.zalo.utilities;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/** Lớp thực hiện tải file từ một đường dẫn
 * ở đây chỉ dùng để tải file audio nên đặt cứng đường dẫn audio*/
public class DownloadFile extends AsyncTask<String, String, String> {
    private String fileName;
    public DownloadFile(String fileName){
        this.fileName = fileName;
    }
    @Override
    public String doInBackground(String... musicURL) {
        int count;
        try {
            URL url = new URL(musicURL[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.KEY_AUDIO_PATH + File.separator + fileName);
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

    }
}
