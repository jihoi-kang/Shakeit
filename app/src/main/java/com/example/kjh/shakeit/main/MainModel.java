package com.example.kjh.shakeit.main;

import com.example.kjh.shakeit.api.ApiClient;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.utils.Serializer;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kjh.shakeit.app.Constant.ERROR_BAD_REQUEST;
import static com.example.kjh.shakeit.app.Constant.ERROR_SERVICE_UNAVAILABLE;
import static com.example.kjh.shakeit.app.Constant.SUCCESS_OK;

public class MainModel implements MainContract.Model {

    private final String TAG = MainModel.class.getSimpleName();

    /**------------------------------------------------------------------
     메서드 ==> 채팅 목록 가져와서 Realm에 저장
     ------------------------------------------------------------------*/
    @Override
    public void getChatLogList(int userId, ResultCallback callback) {
        Call<ResponseBody> result = ApiClient.create().getChatLogList(userId);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case SUCCESS_OK:
                        Realm realm = Realm.getDefaultInstance();
                        try {
                            realm.beginTransaction();
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            for(int index = 0; index < jsonArray.length(); index++) {
                                ChatHolder holder = Serializer.deserialize(jsonArray.getJSONObject(index).toString(), ChatHolder.class);
                                realm.copyToRealm(holder);
                            }
                            realm.commitTransaction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            realm.close();
                        }
                        callback.onSuccess("success");
                        break;
                    case ERROR_SERVICE_UNAVAILABLE: callback.onFailure("SERVICE_UNAVAILABLE"); break;
                    case ERROR_BAD_REQUEST: callback.onFailure("SERVER_ERROR"); break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("" + t.getMessage());
            }
        });
    }
}
