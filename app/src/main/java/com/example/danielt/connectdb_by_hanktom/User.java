package com.example.danielt.connectdb_by_hanktom;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HWT49 on 2016/6/20.
 */
public class User extends Object{

     int success=0,uId=0;

     String describe ="null",account="null",password="null";

    public User(){

    }

    public User(String response){

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

                    String account = obj.getString("account");

                    String password = obj.getString("password");

                    setuId(uId);

                    setAccount(account);

                    setPassword(password);

                    Log.d("parseJSON", uId + "/" + account + "/" + password);

                }
            }
        }catch (JSONException e){

            Log.d("parseJSON", "JSONArray error");

            e.printStackTrace();

        }
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSuccess() {
        return success;
    }

    public String getDescribe() {
        return describe;
    }

    public int getuId() {
        return uId;
    }

    public String getAccount() {
        return this.account;
    }

    public String getPassword() {
        return password;
    }
}
