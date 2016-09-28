package com.example.danielt.connectdb_by_hanktom;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by HWT49 on 2016/6/20.
 */

//為了Diary物件能放進bundle傳送,必須實作Serializable

public class Diary extends Object implements Serializable {

    //diary_text預設內容為no record,用以判斷該篇日記是否有紀錄

    String describe, date, title , mood, diary_text , pic = "pic";

    int success, mark = 0;

    public Diary() {

    }

    //解析PHP傳回的response以建立diary物件

    public Diary(String response) {

        try {

            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                //該response存了兩個陣列，第一個是message，有關傳送成功失敗的訊息

                if (i == 0) {

                    int success = obj.getInt("success");

                    String describe = obj.getString("describe");

                    setSuccess(success);

                    setDescribe(describe);

                    Log.d("parseJSON", success + "/" + describe);

                } else {

                    //第二個是回傳新增的資料

                    int uId = obj.getInt("uId");

                    String date = obj.getString("date");

                    String title = obj.getString("title");

                    String mood = obj.getString("mood");

                    String diary_text = obj.getString("diary_text");

                    String pic = obj.getString("pic");

                    int mark = obj.getInt("mark");

                    //set完各項變數後，diary物件的資料就齊全了，可以提共個別取得diary的資料，如取得標題 getTitle();

                    setDate(date);

                    setTitle(title);

                    setMood(mood);

                    setDiary_text(diary_text);

                    setPic(pic);

                    setMark(mark);

                    Log.d("parseJSON", uId + "/" + date + "/" + title + "/" + mood + "/" + diary_text + "/" + pic + "/" + mark);

                }
            }

        } catch (JSONException e) {

            Log.d("parseJSON", "JSONArray error");

            e.printStackTrace();

        }
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public void setDiary_text(String diary_text) {
        this.diary_text = diary_text;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getDescribe() {
        return describe;
    }

    public int getSuccess() {
        return success;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getMood() {
        return mood;
    }

    public String getDiary_text() {
        return diary_text;
    }

    public String getPic() {
        return pic;
    }

    public int getMark() {
        return mark;
    }

}
