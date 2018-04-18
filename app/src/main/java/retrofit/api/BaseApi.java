package retrofit.api;

import org.json.JSONObject;

import java.util.List;

import WedoneBioVein.UserData;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xyuxiao on 2016/9/23.
 */
public interface BaseApi {

    @POST("get")
    Observable<JSONObject> uploadPhotoBase(
            @Query("hhh") byte[] regTemplateData
    );


}

