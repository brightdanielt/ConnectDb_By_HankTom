package com.example.danielt.connectdb_by_hanktom;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HWT49 on 2016/6/28.
 */
public class Message {

    int success=0;
    String describe;


    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getSuccess() {
        return success;
    }

    public String getDescribe() {
        return describe;
    }

    public Message(){

    }

    public Message(String response) {

        try {

            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                //該response存了至少一個陣列，第一個是message，有關傳送成功失敗的訊息

                if (i == 0) {

                    int success = obj.getInt("success");

                    String describe = obj.getString("describe");

                    setSuccess(success);

                    setDescribe(describe);

                    Log.d("parseJSON", "success:"+success + "/" + "describe"+describe);

                } else {

                    //沒有第二個回傳的資料

                }
            }
        }catch (JSONException e){

            Log.d("parseJSON", "JSONArray error");

            e.printStackTrace();

        }
    }
}
