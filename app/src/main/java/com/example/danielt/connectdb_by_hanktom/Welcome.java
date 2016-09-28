package com.example.danielt.connectdb_by_hanktom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class Welcome extends AppCompatActivity {

    private String account, password, request_data, date;

    Login myLogin;

    final String LOGIN = "http://218.161.15.149:8080/AndroidConnectDB/login.php";

    private final int CHECK_INTERNET_WHEN_LOAD = 1;

    RelativeLayout welcome_layout;

    TextView tv1;

    My_alert_dialog myDialog;

    Thread login_thread;

    private int err_count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

        setContentView(R.layout.activity_welcome);

        doFindView();

        setViewEffect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d("welcome", "waiting_3sec");

                myDialog = new My_alert_dialog(Welcome.this) {

                    @Override
                    boolean action_after_dialog() {

                        autoLogin();

                        Log.d("thread", "after_autoLogin");

                        return true;

                    }
                };

                myDialog.check_internet_dialog();

            }
        }, 3000);


//        welcome_wait();

    }

    private void doFindView() {

        welcome_layout = (RelativeLayout) findViewById(R.id.welcome_layout);

        tv1 = (TextView) findViewById(R.id.tv_1);

    }

    private void setViewEffect() {

        welcome_layout.setEnabled(false);

        tv1.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                tv1.setText("點ㄌ");

            }
        });

    }

    private void ask_flip() {

        welcome_layout.setEnabled(true);

        welcome_layout.setOnTouchListener(new View.OnTouchListener() {

            float currentX, currentY, latX = -1, lastY = -1, distance = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

//                    case MotionEvent.ACTION_MOVE: {
//
//                        currentX=event.getX();
//
//                        currentY=event.getY();
//
//                        if(latX<0 && lastY<0){
//
//                            latX=currentX;
//
//                            lastY=currentY;
//
//                        }
//                        else {
//
//                            distance=(float)Math.sqrt((latX-currentX)*(latX-currentX)+(lastY-currentY)*(lastY-currentY));
//
//                            if(distance>300.0F){
//
//                                Intent intent = new Intent();
//
//                                Bundle bundle = new Bundle();
//
//                                bundle.putSerializable("myLogin", myLogin);
//
//                                bundle.putString("account", account);
//
//                                intent.putExtras(bundle);
//
//                                intent.setClass(Welcome.this, Lobby.class);
//
//                                startActivity(intent);
//
////                                finish();
//
//                            }
//
//                        }
//
//                        break;
//
//                    }

                }

                return true;
            }
        });

    }

    private void welcome_wait() {


    }

    @Override
    public void onBackPressed() {
        //取消登入時按下返回鍵而結束程式
    }

    private void autoLogin() {

        myDialog.data_loading_dialog();

        Log.d("thread", "in_autoLogin");

        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);

        account = sharedPreferences.getString("account", "noAccount");

        password = sharedPreferences.getString("password", "noPassword");


        //有登入紀錄
        if (account != "noAccount") {

//            btn_login.setEnabled(false);

            tv1.setText("ac:" + account + "\n" + "ps:" + password);

//            ed_account.setText(account);
//
//            ed_password.setText(password);

            getDate();

            setRequest_data();

            login();

            Log.d("thread", "login");

        } else {

            myDialog.data_loading_dialog.setCancelable(true);

            myDialog.data_loading_dialog.cancel();

            tv1.setText("轉跳註冊頁面...");

            Intent intent = new Intent();

            intent.setClass(Welcome.this, Register.class);

            startActivity(intent);

            finish();

        }

    }

    //有登入紀錄的情況才調用 login()
    public void login() {

//在新的執行緒中才能使用回傳的資料對UI做更新

        Log.d("thread", "in_login");

        login_thread = new Thread(new Runnable() {

            @Override

            public void run() {

                Log.d("thread", "login_thread_run");

                //取得輸入內容，登入成功時用於儲存登入資訊

//                account = ed_account.getText().toString();
//
//                password = ed_password.getText().toString();

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {
                    err_count++;
//                    while(err_count++>2){
//
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(Welcome.this,"連線失敗已達"+err_count+"次",Toast.LENGTH_LONG).show();
//                            }
//                        });
//                        break;
//
//
//
//                    }

                    ConnectDb connectDb = new ConnectDb();

                    //把傳回的Object轉回User

                    myLogin = (Login) connectDb.askConnect(LOGIN, request_data);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {
                        public void run() {

                            myDialog.data_loading_dialog.setCancelable(true);

                            myDialog.data_loading_dialog.cancel();

                            //要改變介面的程式寫在這裡
                            if (myLogin == null) {

                                //重新檢查網路，並呼叫 autoLogin()
                                myDialog.check_internet_dialog();

                            } else if (myLogin.getSuccess() == 1) {

                                myDialog.data_loading_dialog.setCancelable(true);

                                myDialog.data_loading_dialog.cancel();

                                tv1.setVisibility(View.VISIBLE);

                                tv1.setText("已登入");

                                Intent intent = new Intent();

                                Bundle bundle = new Bundle();

                                bundle.putSerializable("myLogin", myLogin);

                                bundle.putString("account", account);

                                intent.putExtras(bundle);

                                intent.setClass(Welcome.this, Lobby.class);

                                startActivity(intent);

                                finish();

//                                myDialog.data_loading_dialog.setCancelable(true);
//
//                                myDialog.data_loading_dialog.cancel();


                            } else if (myLogin.getSuccess() == 0) {

                                tv1.setText("轉跳註冊頁面...");

                                Intent intent = new Intent();

                                intent.setClass(Welcome.this, Register.class);

                                startActivity(intent);

                                finish();

                            } else {

                                //重新檢查網路，並呼叫 autoLogin()
                                myDialog.check_internet_dialog();

                            }

                        }
                    });
                } catch (Exception e) {

                    Log.d("LOGIN_ERR", err_count + "");
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
        });
        login_thread.start();


    }

    private void getDate() {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        date = sDateFormat.format(new java.util.Date());

    }

    private void setRequest_data() {

        request_data = account +

                "\n" + password +

                "\n" + date;

    }
}
