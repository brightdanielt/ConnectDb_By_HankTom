package com.example.danielt.connectdb_by_hanktom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class Create_friend_invitation extends AppCompatActivity {

    TextView tv_uId;

    EditText ed_target_id;

    Button btn_submit;

    int uId, target_id;

    String invitation_date, request_data;

    Message message;

    Intent intent;

    Bundle bundle;

    final String CREATE_FRIEND_INVITATION = "http://218.161.15.149:8080/AndroidConnectDB/create_friend_invitation.php";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.alpha_0to1,R.anim.alpha_1to0);

        setContentView(R.layout.activity_create_friend_invitation);

        getDataFromBundle();

        doFindView();

        setViewEffect();

    }

    public void doFindView() {

        tv_uId = (TextView) findViewById(R.id.tv_uId);

        ed_target_id = (EditText) findViewById(R.id.ed_target_id);

        btn_submit = (Button) findViewById(R.id.btn_submit);

    }

    public void setViewEffect() {

        //限制輸入欄位長度

//        ed_target_id.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        tv_uId.setText(uId+"");

        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if ((ed_target_id.getText().toString()).equals("")) {

                    AlertDialog.Builder input_null = new AlertDialog.Builder(Create_friend_invitation.this);

                    input_null.setTitle("有事嗎..")

                            .setMessage("ID還沒輸入")

                            .setIcon(R.mipmap.ic_launcher)

                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })

                            .show();

                } else {

                    target_id = Integer.valueOf(ed_target_id.getText().toString());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    String date = sdf.format(new java.util.Date());

                    invitation_date = date;

                    sent_friend_invitation();

                }
            }
        });

    }

    private void getDataFromBundle() {

        intent = getIntent();

        bundle = intent.getExtras();

        uId = bundle.getInt("uId");

    }

    public void sent_friend_invitation() {

        //在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {

            @Override

            public void run() {

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    ConnectDb connectDb = new ConnectDb();

                    setRequestData();

                    //將呼叫的PHP檔及日記內容傳給askConnect

                    //回傳為Object型態，將他轉為Diary以使用日記內容

                    message = (Message) connectDb.askConnect(CREATE_FRIEND_INVITATION, request_data);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            //對UI的變更，可以寫在另一個方法，呼叫使用

                            show_message();

                        }
                    });

                } catch (Exception e) {

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

    //顯示發送好友邀請成功與否
    public void show_message() {

        String message_title;

        AlertDialog.Builder show_message = new AlertDialog.Builder(Create_friend_invitation.this);

        int success = message.getSuccess();

        if (success == 1) {

            message_title = "成功";

        } else {

            message_title = "哎呀!";

        }
        show_message.setTitle(message_title)

                .setIcon(R.mipmap.ic_launcher)

                .setMessage(message.getDescribe())

                .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                    }

                })

                .show();

    }

    private void setRequestData() {

        //設定要傳給PHP的內容

        request_data = uId +

                "\n" + target_id +

                "\n" + invitation_date;

    }

}
