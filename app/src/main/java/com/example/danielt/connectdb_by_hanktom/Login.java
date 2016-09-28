package com.example.danielt.connectdb_by_hanktom;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by HWT49 on 2016/7/27.
 */
public class Login implements Serializable{

    private int uId,success=0;

    private String describe,last_login;

    public Login() {
    }

    public Login(String response) {

        try {

            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                if (i == 0) {

                    int success = obj.getInt("success");

                    String describe = obj.getString("describe");

                    setSuccess(success);

                    setDescribe(describe);

                    Log.d("parseJSON", success + "/" + describe);

                } else {

                    int uId = obj.getInt("uId");

                    String last_login= obj.getString("last_login");

                    setuId(uId);

                    setLast_login(last_login);

                    Log.d("parseJSON", uId + "/" + last_login );

                }
            }

        }catch (JSONException e){

            Log.d("parseJSON", "JSONArray error");

            e.printStackTrace();

        }

    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public int getuId() {
        return uId;
    }

    public int getSuccess() {
        return success;
    }

    public String getDescribe() {
        return describe;
    }

    public String getLast_login() {
        return last_login;
    }
}
