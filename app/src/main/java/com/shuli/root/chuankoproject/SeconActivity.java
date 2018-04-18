package com.shuli.root.chuankoproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONObject;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Api;
import retrofit.ConnectUrl;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SeconActivity  extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
    }


        @OnClick({ R.id.doSend})
        public void sayHi(TextView button) {
            switch (button.getId()) {
                case R.id.doSend:

           //         upload("收到请求了没有？");
                    Log.i("sss",">>>>>>>>>>>>");
                    break;
            }
        }

//        private void upload(String message){
//            Api.getBaseApiWithOutFormat(ConnectUrl.URL)
//                    .uploadPhotoBase(message)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<JSONObject>() {
//                                   @Override
//                                   public void call(JSONObject jsonObject) {
//                                       Log.i("sss",jsonObject.toString());
//
//                                   }
//                               }, new Action1<Throwable>() {
//                                   @Override
//                                   public void call(Throwable throwable) {
//                                       Log.i("sss",throwable.toString());
//
//                                   }
//                               }
//                    );
//        }

    };

