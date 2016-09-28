package com.example.danielt.connectdb_by_hanktom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Check_friend_invitation extends AppCompatActivity {

    Friend_invitation friend_invitation;

    final String CHECK_FRIEND_INVITATION="http://218.161.15.149:8080/AndroidConnectDB/check_friend_invitation.php";

    final String CREATE_A_FRIEND="http://218.161.15.149:8080/AndroidConnectDB/create_a_friend.php";

    String request_data,friend_date;

    int uId,fId,authority_level=1;

    Message message;

    TextView tv;

    ListView friend_invitation_listView;

    String[] invitation_from_id;

    String[] sent_invitation_date;

    Intent intent;

    Bundle bundle;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.alpha_0to1,R.anim.alpha_1to0);

        setContentView(R.layout.activity_check_friend_invitation);

        getDataFromBundle();

        doFindView();

        //直接查詢好友邀請

        open_friend_invitation();

    }

    private void doFindView(){

        friend_invitation_listView= (ListView) findViewById(R.id.friend_invitation_listView);

        tv= (TextView) findViewById(R.id.textView1);

    }

    private void getDataFromBundle(){

        intent = getIntent();

        bundle = intent.getExtras();

        uId = bundle.getInt("uId");

    }

    public void open_friend_invitation(){

        //在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {

            @Override

            public void run() {

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    ConnectDb connectDb = new ConnectDb();

                    //設定要傳給PHP的內容

                    request_data=uId+"\n";

                    //將呼叫的PHP檔及日記內容傳給askConnect

                    //回傳為Object型態，將他轉為Diary以使用日記內容

                    friend_invitation = (Friend_invitation)connectDb.askConnect(CHECK_FRIEND_INVITATION,request_data);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable(){

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            //對UI的變更，可以寫在另一個方法，呼叫使用

                                setAdapter();

                        }});

                }

                catch (Exception e){

                    e.printStackTrace();

                }

                /*
                * Looper 主要的用途是把 Thread 轉變成 Pipe line，可以說是另開一條線程去跑，這樣就不會影響主線程的運行。
                *
                * 而 Handler 是把線程加入一些 message 讓開發者可以利用發送 message來控制此線程。
                *                 *
                * */

                Looper.loop();

            }

        }).start();

    }

    public void setAdapter(){

        if(friend_invitation.getSuccess()==0){

            tv.setText("目前沒有任何的邀請!");

            String[] no_invitation={};

            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,no_invitation);

            friend_invitation_listView.setAdapter(arrayAdapter);

        }

        else {

            invitation_from_id=friend_invitation.getArray_invitation_from_id();

            sent_invitation_date=friend_invitation.getArray_sent_invitation_date();

            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,invitation_from_id);

            friend_invitation_listView.setAdapter(arrayAdapter);

            friend_invitation_listView.setOnItemClickListener(new ListView.OnItemClickListener() {

                @Override

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String from_id=parent.getItemAtPosition(position).toString();

                    fId=Integer.valueOf(from_id);

                    authority_level=1;

                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    String date=sdf.format(new java.util.Date());

                    friend_date=date;

//                Toast.makeText(Check_friend_invitation.this,"id:"+from_id.toString()+"想加你為好友",Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder submit_friend=new AlertDialog.Builder(Check_friend_invitation.this);

                    submit_friend.setTitle("加個好友吧!")

                            .setIcon(R.mipmap.ic_launcher)

                            .setMessage("要接受ID:"+from_id+"的好友邀請嗎?")

                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialog, int which) {

                                    create_a_friend();

                                }

                            })

                            .setNegativeButton("取消",new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })

                            .show();

                }

            });

        }

    }

    public void create_a_friend(){

        //在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {

            @Override

            public void run() {

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    ConnectDb connectDb = new ConnectDb();

                    //設定要傳給PHP的內容

                    request_data=uId+

                            "\n"+fId+

                            "\n"+authority_level+

                            "\n"+friend_date;

                    //將呼叫的PHP檔及日記內容傳給askConnect

                    //回傳為Object型態，將他轉為Diary以使用日記內容

                    message = (Message)connectDb.askConnect(CREATE_A_FRIEND,request_data);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable(){

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            //對UI的變更，可以寫在另一個方法，呼叫使用

                            show_message();

                        }});

                }

                catch (Exception e){

                    e.printStackTrace();

                }

                /*
                * Looper 主要的用途是把 Thread 轉變成 Pipe line，可以說是另開一條線程去跑，這樣就不會影響主線程的運行。
                *
                * 而 Handler 是把線程加入一些 message 讓開發者可以利用發送 message來控制此線程。
                *                 *
                * */

                Looper.loop();

            }

        }).start();

    }

    //顯示加好友成功與否

    public void show_message(){

        String message_title;

        AlertDialog.Builder submit_friend=new AlertDialog.Builder(Check_friend_invitation.this);

        int success=message.getSuccess();

        if(success==1){

            message_title="成功";

        }

        else {

            message_title="出現了一些問題";

        }

        submit_friend.setTitle(message_title)

                .setIcon(R.mipmap.ic_launcher)

                .setMessage(message.getDescribe())

                .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        //再次載入好友邀請，等塗於刷新好友邀請清單

                        open_friend_invitation();

                    }

                })

                .show();
    }

}











//本類別用於接收response回的資料，並轉成本類別格式
class Friend_invitation{

    ArrayList <String> arrayList_invitation_from_id=new ArrayList<String>();

    ArrayList<String> arrayList_sent_invitation_date=new ArrayList<String>();

    String[] array_invitation_from_id;

    String[] array_sent_invitation_date;

    int success;

    String describe;

    Friend_invitation(){

    }

    public Friend_invitation(String response) {

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

                    //第二個及以後的陣列是回傳好友邀請

                    int from_int_id=obj.getInt("invitation_from_id");

                    String invitation_from = String.valueOf(from_int_id);

                    String sent_invitation_date = obj.getString("date");

                    //set完各項變數後，friend_invitation　物件的資料就齊全了，

                    // 可以提供個別取得　array_invitation_from_id　的資料，如取得發送邀請的id getArray_invitation_from_id();

                    //後續再放入　listView

                    //將　invitation_from　加入可擴充陣列中

                    setArrayList_invitation_from_id(invitation_from);

                    //將　sent_invitation_date　加入可擴充陣列中

                    setArrayList_sent_invitation_date(sent_invitation_date);

                }
            }

            //取出 存放 invitation_from 的arrayList 資料，並存成陣列String

            getArray_invitation_from_id();

            //取出 存放 sent_invitation_date 的arrayList 資料，並存成陣列String

            getArray_sent_invitation_date();

//            Log.d("parseJSON",  array_invitation_from_id[0] + "/" + array_sent_invitation_date[0]);

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

    public int getSuccess() {

        return success;

    }

    public String getDescribe() {

        return describe;

    }

    public void setArrayList_invitation_from_id(String invitation_from_id) {

        this.arrayList_invitation_from_id.add(invitation_from_id) ;

    }

    public void setArrayList_sent_invitation_date(String sent_invitation_date) {

        this.arrayList_sent_invitation_date.add(sent_invitation_date);

    }

    public ArrayList <String> getArrayList_invitation_from_id() {

        return arrayList_invitation_from_id;

    }

    public ArrayList <String> getArrayList_senting_invitation_date() {

        return arrayList_sent_invitation_date;

    }

    public String[] getArray_invitation_from_id() {

        arrayList_invitation_from_id.trimToSize();

        int length=arrayList_invitation_from_id.size();

        array_invitation_from_id=new String[length];

        for(int index=0;index<length;index++){

            array_invitation_from_id[index]=arrayList_invitation_from_id.get(index);

        }

        return  array_invitation_from_id;

    }

    public String[] getArray_sent_invitation_date() {

        arrayList_sent_invitation_date.trimToSize();

        int length=arrayList_sent_invitation_date.size();

        array_sent_invitation_date=new String[length];

        for(int index=0;index<length;index++){

            array_sent_invitation_date[index]=arrayList_sent_invitation_date.get(index);

        }

        return  array_sent_invitation_date;

    }
}