package retrofit.api;

import com.shuli.root.chuankoproject.activity.MessageFinger;

import org.json.JSONObject;

import java.util.List;

import WedoneBioVein.UserData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xyuxiao on 2016/9/23.
 */
public interface BaseApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("venousInfoAdd")
    Observable<JSONObject> uploadPhotoBase3(
            @Body RequestBody body
    );


}

