package com.example.danielt.connectdb_by_hanktom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Load_friend_list extends AppCompatActivity {

    final String LOAD_FRIEND_LIST = "http://218.161.15.149:8080/AndroidConnectDB/load_friend_list.php";

    String request_data, toWhichActivity;

    Friend_list friend_list;

    int uId;

    String[] friend_full_name, photo_sticker;

    int[] friend_id;

    ListView friend_listView;

    private My_alert_dialog loading_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load_friend_list);

        getDataFromBundle();

        doFindView();

        setRequest_data();

        loading_dialog = new My_alert_dialog(Load_friend_list.this) {
            @Override
            boolean action_after_dialog() {

                load_friend_list();

                return true;

            }
        };

        loading_dialog.check_internet_dialog();

    }

    private void getDataFromBundle() {

        Intent it = getIntent();

        Bundle bd = it.getExtras();

        //接收開啟時來自Load_friend_list夾帶的bundle資料

        toWhichActivity = bd.getString("toWhichActivity");

        uId = bd.getInt("uId");

        Log.d("getDataFromBundle", "uId:" + uId);

    }

    private void doFindView() {

        friend_listView = (ListView) findViewById(R.id.friend_listview);


    }

    public void load_friend_list() {

        loading_dialog.data_loading_dialog();

        //在新的執行緒中才能使用回傳的資料對UI做更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error
                try {

                    ConnectDb connectDb = new ConnectDb();
                    //將呼叫的PHP檔及日記內容傳給askConnect
                    //回傳為Object型態，將他轉為Diary以使用日記內容
                    friend_list = (Friend_list) connectDb.askConnect(LOAD_FRIEND_LIST, request_data);

//                    final String title=diary.getTitle().toString();
                    //更改UI畫面的程式碼必須由UI的執行續執行
                    runOnUiThread(new Runnable() {
                        //要改變介面的程式寫在run()裡面
                        public void run() {

                            if (friend_list.getSuccess() == 0) {

                                AlertDialog.Builder no_diary = new AlertDialog.Builder(Load_friend_list.this);

                                no_diary.setTitle("咦?")

                                        .setMessage("你還沒有任何朋友QQ")

                                        .setIcon(R.mipmap.ic_launcher)

                                        .setCancelable(false)

                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

//                                                Intent intent = new Intent();
//
//                                                Bundle bundle = new Bundle();
//
//                                                bundle.putString("toWhichActivity", "Simple_browse_diary");
//
//                                                intent.putExtras(bundle);
//
//                                                intent.setClass(Simple_browse_diary.this, Load_friend_list.class);
//
//                                                startActivity(intent);

                                                finish();

                                            }
                                        })

                                        .show();

                            } else {

                                loading_dialog.data_loading_dialog.setCancelable(true);

                                loading_dialog.data_loading_dialog.cancel();

                                //對日記內容的應用
                                show_listView();

                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                * Looper 主要的用途是把 Thread 轉變成 Pipe line，可以說是另開一條線程去跑，這樣就不會影響主線程的運行。
                *
                * 而 Handler 是把線程加入一些 message 讓開發者可以利用發送 message來控制此線程。
                *
                * */
                Looper.loop();
            }
        }).start();
    }

    public void setRequest_data() {

        //取得日記的內容
        request_data = uId + "";
        Log.d("load_diary", request_data);
    }

    public void show_listView() {

        friend_id = friend_list.getFriend_id();

        friend_full_name = friend_list.getFull_name();

        Friend_list_Adapter friend_list_adapter = new Friend_list_Adapter(Load_friend_list.this);

        friend_listView.setAdapter(friend_list_adapter);

        friend_listView.startAnimation(AnimationUtils.loadAnimation(Load_friend_list.this, R.anim.translate_rtol));

        friend_listView.setOnItemClickListener(listViewListener);

    }

    private ListView.OnItemClickListener listViewListener = new ListView.OnItemClickListener() {

        @Override

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (toWhichActivity.equals("Simple_browse_diary")) {

                int selected_fId = friend_id[position];

                Log.d("selected_fId", "selected_fId:" + selected_fId);

                Intent intent = new Intent();

                Bundle bundle = new Bundle();

                bundle.putInt("fId", selected_fId);

//                bundle.putInt("uId", uId);

                if (selected_fId == uId) {

                    bundle.putInt("authority", 1);

                } else {

                    bundle.putInt("authority", 0);

                }

                intent.putExtras(bundle);

                intent.setClass(Load_friend_list.this, Simple_browse_diary.class);

                startActivity(intent);

//                finish();

            }

            if (toWhichActivity.equals("Load_friend_info")) {

                int selected_fId = friend_id[position];

                Intent intent = new Intent();

                Bundle bundle = new Bundle();

                bundle.putInt("uId", uId);

                bundle.putInt("fId", selected_fId);

//                bundle.putInt("authority", 0);

                intent.putExtras(bundle);

                intent.setClass(Load_friend_list.this, Load_friend_info.class);

                startActivity(intent);

                Log.d("selected_fId", "selected_fId:" + selected_fId);

//                finish();
            }
        }
    };


    class Friend_list_Adapter extends BaseAdapter {

        private LayoutInflater myInflater;

        public Friend_list_Adapter(Context c) {

            myInflater = LayoutInflater.from(c);

        }

        @Override

        public int getCount() {

            return friend_id.length;

        }

        @Override

        public Object getItem(int position) {

            return null;

        }

        @Override

        public long getItemId(int position) {

            return position;

        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = myInflater.inflate(R.layout.friend_listview, null);

            ImageView img_photo_sticker = (ImageView) convertView.findViewById(R.id.img_photo_sticker);

            TextView tv_fId = (TextView) convertView.findViewById(R.id.tv_friend_id);

            TextView tv_full_name = (TextView) convertView.findViewById(R.id.tv_full_name);


//            img_photo_sticker.setImageResource();
            tv_fId.setText(String.valueOf(friend_id[position]));

            tv_full_name.setText(friend_full_name[position]);

            return convertView;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.alpha_0to1, R.anim.alpha_1to0);
    }
}

class Friend_list {

    String[] photo_sticker, full_name;

    String describe;

    int[] friend_id;

    int success = 0;

    Friend_list() {

    }

    Friend_list(String response) {

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
                }
                if (i == 1) {
                    //第二個是回傳新增的資料
                    int length = obj.length();
                    Log.d("lengthOfFid", length + "");

                    int[] friend_id = new int[length];

                    for (int index = 1; index <= length; index++) {

                        friend_id[index - 1] = obj.getInt("" + index);

                    }

                    setFriend_id(friend_id);
                    int[] a = getFriend_id();
                    Log.d("fID", "a[0]:" + a[0]);
                }
                if (i == 2) {


                    int length = obj.length();

                    String[] full_name = new String[length];

                    for (int index = 1; index <= length; index++) {

                        full_name[index - 1] = obj.getString("" + index);
                    }
                    setFull_name(full_name);
                }
                if (i == 3) {

                    int length = obj.length();

                    String[] photo_sticker = new String[length];

                    for (int index = 1; index <= length; index++) {

                        photo_sticker[index - 1] = obj.getString(index + "");

                    }

                    setPhoto_sticker(photo_sticker);
                }
                //set完各項變數後，diary物件的資料就齊全了，可以提共個別取得diary的資料，如取得標題 getTitle();
            }
        } catch (JSONException e) {
            Log.d("parseJSON", "JSONArray error");
            e.printStackTrace();
        }

    }

    public void setFull_name(String[] full_name) {
        this.full_name = full_name;
    }

    public void setFriend_id(int[] friend_id) {
        this.friend_id = friend_id;
    }

    public void setPhoto_sticker(String[] photo_sticker) {
        this.photo_sticker = photo_sticker;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int[] getFriend_id() {
        return friend_id;
    }

    public String[] getPhoto_sticker() {
        return photo_sticker;
    }

    public String[] getFull_name() {
        return full_name;
    }

    public String getDescribe() {
        return describe;
    }

    public int getSuccess() {
        return success;
    }
}
