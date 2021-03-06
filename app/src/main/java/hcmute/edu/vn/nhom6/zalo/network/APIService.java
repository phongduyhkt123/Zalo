package hcmute.edu.vn.nhom6.zalo.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APIService {
    @POST("send") // gửi thông báo với phương thức post
    Call<String> sendMessage(
            @HeaderMap HashMap<String, String> headers,
            @Body String messageBody
            );
}
