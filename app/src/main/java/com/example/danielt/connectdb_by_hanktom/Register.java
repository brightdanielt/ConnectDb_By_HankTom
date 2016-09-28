package com.example.danielt.connectdb_by_hanktom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Random;

public class Register extends AppCompatActivity {

    final String CREATE_USER = "http://218.161.15.149:8080/AndroidConnectDB/create_user.php";

    final String LOGIN = "http://218.161.15.149:8080/AndroidConnectDB/login.php";

    final String SENT_VERIFICATION_CODE = "http://218.161.15.149:8080/AndroidConnectDB/sent_verification_code.php";

    Button btn_login, btn_register, btn_sent_verification_code, btn_reset_account;

    EditText ed_register_account, ed_register_password, ed_login_account, ed_login_password;

    TextView tv1;

    String account, password, date, request_data;

    User user;

    Login myLogin;

    Message message;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_registor);

        doFindView();

        setViewEffect();

    }

    private void doFindView() {

        ed_register_account = (EditText) findViewById(R.id.ed_register_account);

        ed_register_password = (EditText) findViewById(R.id.ed_register_password);

        ed_login_account = (EditText) findViewById(R.id.ed_login_account);

        ed_login_password = (EditText) findViewById(R.id.ed_login_password);

        btn_login = (Button) findViewById(R.id.btn_login);

        btn_register = (Button) findViewById(R.id.btn_register);

        btn_sent_verification_code = (Button) findViewById(R.id.btn_sent_verification_code);

        btn_reset_account = (Button) findViewById(R.id.btn_reset_account);

        tv1 = (TextView) findViewById(R.id.tv1);

    }

    private void setViewEffect() {

//        btn_login.setEnabled(false);

        btn_register.setEnabled(false);

        btn_sent_verification_code.setEnabled(false);

        btn_reset_account.setEnabled(false);

        ed_register_password.setEnabled(false);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ed_register_password.getText().toString().equals("") || ed_register_password.getText().toString().equals(null)) {

                    tv1.setText(R.string.wrong_password_format);

                } else {

                    ed_register_password.setEnabled(false);

                    //這裡不使用 setRequestData() ,註冊帳號不代表註冊成功, requestData 不加入當下日期

                    account = ed_register_account.getText().toString();

                    password = ed_register_password.getText().toString();

                    request_data = account + "\n" + password;

                    register();

                    Log.d("register", "register finished...");

                }

            }
        });

        btn_reset_account.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                ed_register_account.setEnabled(true);

                ed_register_password.setEnabled(false);

                btn_reset_account.setEnabled(false);

                btn_login.setEnabled(false);

                btn_register.setEnabled(false);

                btn_sent_verification_code.setEnabled(false);

                if(isEmail(ed_login_account.getText().toString())){

                    btn_sent_verification_code.setEnabled(true);

                }

            }

        });

        btn_sent_verification_code.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


//                String ac = ed_account.getText().toString();

                send_verification_code();

            }

        });

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                account=ed_login_account.getText().toString();

                password=ed_login_password.getText().toString();

                if(isEmail(account)){

                    if(password.equals("") || password.equals(null)) {

                        Toast toast=Toast.makeText(Register.this,R.string.wrong_password_format,Toast.LENGTH_LONG);

                        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                        toast.show();

                    }
                    else {

                        login();

                    }

                }
                else{

//                    btn_login.setEnabled(false);

                    Toast toast=Toast.makeText(Register.this,R.string.wrong_account_format,Toast.LENGTH_LONG);

                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                    toast.show();

                }

            }
        });

        ed_register_account.addTextChangedListener(new TextWatcher() {

            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                String ac = ed_register_account.getText().toString();

                if (isEmail(ac)) {

//                    email_switch.setChecked(true);

                    tv1.setText("");

                    btn_sent_verification_code.setEnabled(true);

                } else {

//                    email_switch.setChecked(false);

                    tv1.setText("E-mail格式錯誤");

                    btn_sent_verification_code.setEnabled(false);

                }

            }
        });

    }

    public static boolean isEmail(String email) {

        if (null == email || "".equals(email)) return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public void register() {

        //在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    ConnectDb connectDb = new ConnectDb();

                    //把傳回的Object轉回User

                    user = (User) connectDb.askConnect(CREATE_USER, request_data);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {
                        public void run() {

                            //要改變介面的程式寫在這裡

                            tv1.setText("success:" + user.getSuccess() +

                                    "\n" + "describe" + user.getDescribe() +

                                    "\n" + "account:" + user.getAccount() +

                                    "\n" + "password" + user.getPassword());

                            if(user.getSuccess() == 0){ //帳號已被註冊過

                                ed_register_account.setEnabled(true);

                                ed_register_password.setEnabled(false);

                                btn_reset_account.setEnabled(true);

                                btn_reset_account.setEnabled(false);

                                btn_register.setEnabled(false);

                                Toast toast=Toast.makeText(Register.this,R.string.register_ac_repeat,Toast.LENGTH_LONG);

                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                                toast.show();


                            }
                            else if (user.getSuccess() == 1) {  //註冊成功，並登入

                                btn_reset_account.setEnabled(false);

                                btn_register.setEnabled(false);

//                                btn_removeLoginInfo.setEnabled(true);

                                login();

                                Toast.makeText(Register.this,R.string.register_success,Toast.LENGTH_LONG).show();

                            }

                            else if(user.getSuccess() == 2){    //新增帳號失敗，再試一次

                                Toast.makeText(Register.this,R.string.register_create_failed,Toast.LENGTH_LONG).show();

                            }
                            else if(user.getSuccess() == 3){    //網路流量異常，請稍後再嘗試

                                Toast.makeText(Register.this,R.string.internet_problem,Toast.LENGTH_LONG).show();

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
                *                 *
                * */
                Looper.loop();

            }
        }).start();

    }

    public void login() {

//在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {

            @Override

            public void run() {

                //取得輸入內容，登入成功時用於儲存登入資訊

//                account = ed_account.getText().toString();
//
//                password = ed_password.getText().toString();

                getDate();

                setRequest_data();

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    ConnectDb connectDb = new ConnectDb();

                    //把傳回的Object轉回User

                    myLogin = (Login) connectDb.askConnect(LOGIN, request_data);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {
                        public void run() {

                            //要改變介面的程式寫在這裡


                            if (myLogin.getSuccess() == 0) {

                                Toast toast=Toast.makeText(Register.this,R.string.login_account_not_exist,Toast.LENGTH_LONG);

                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                                toast.show();

                            }
                            else if (myLogin.getSuccess() == 1) {

                                tv1.setText(myLogin.getSuccess() + "\n" + myLogin.getDescribe() + "\n" + myLogin.getuId() + "\n" + myLogin.getLast_login());

                                Toast toast=Toast.makeText(Register.this,R.string.login_success,Toast.LENGTH_LONG);

                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                                toast.show();

                                saveLogin();

                                Intent intent=new Intent();

                                Bundle bundle=new Bundle();

                                bundle.putSerializable("myLogin",myLogin);

                                bundle.putString("account",account);

                                bundle.putString("password",password);

                                intent.putExtras(bundle);

                                intent.setClass(Register.this,Lobby.class);

                                startActivity(intent);

                                finish();

                            }
                            else if (myLogin.getSuccess() == 2) {

                                Toast toast=Toast.makeText(Register.this,R.string.login_ps_not_correct,Toast.LENGTH_LONG);

                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                                toast.show();

                                saveLogin();

                            }
                            else if (myLogin.getSuccess() == 3) {

                                Toast toast=Toast.makeText(Register.this,R.string.internet_problem,Toast.LENGTH_LONG);

                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);

                                toast.show();

                                saveLogin();

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
                *                 *
                * */
                Looper.loop();

            }
        }).start();

    }

    private void getDate() {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        date = sDateFormat.format(new java.util.Date());

    }

    private void saveLogin() {

        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("account", account);

        editor.commit();

        editor.putString("password", password);

        editor.commit();

//        btn_removeLoginInfo.setEnabled(true);

        Log.d("saveLogin", "ac:" + account + " ps:" + password);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   //確定按下退出鍵and防止重複按下退出鍵

            ask_leave_dialog();

        } else {

            finish();

        }

        return false;

    }

    private void ask_leave_dialog() {

        android.support.v7.app.AlertDialog.Builder save_info_dialog = new android.support.v7.app.AlertDialog.Builder(Register.this);

        save_info_dialog.setTitle("想問問你")

                .setIcon(R.mipmap.ic_launcher)

                .setMessage("離開MyDiary?")

                .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        finish();

                    }
                })
                .setNegativeButton("還沒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    private void send_verification_code() {

        Random rd_verification_code = new Random();

        final int verification_code = 1 + rd_verification_code.nextInt(10000);

        //在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {

            @Override

            public void run() {

                //取得輸入內容，登入成功時用於儲存登入資訊

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    ConnectDb connectDb = new ConnectDb();

                    //把傳回的Object轉回User

                    message = (Message) connectDb.askConnect(SENT_VERIFICATION_CODE, request_data =

                            ed_register_account.getText().toString() +

                                    "\n" + verification_code);

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {
                        public void run() {

                            if (message.getSuccess() == 1) {

                                Toast.makeText(Register.this, "請輸入驗證碼" + verification_code, Toast.LENGTH_LONG).show();

                                ask_input_verification_code(String.valueOf(verification_code));


                            } else {

                                Toast.makeText(Register.this, "發生了一些問題，請再傳送一次驗證碼", Toast.LENGTH_LONG).show();

                            }

                            //要改變介面的程式寫在這裡


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

    private void ask_input_verification_code(final String verification_code) {

        final String correct_verification_code = verification_code;

        LayoutInflater layoutInflater = LayoutInflater.from(Register.this);

        final View view = layoutInflater.inflate(R.layout.my_alert_dialog, null);

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(Register.this);

        myAlertDialog.setTitle("驗證碼已寄出,請至您的E-MAIL收信")

                .setView(view)

                .setMessage("輸入驗證碼")

                .setCancelable(false)

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        EditText ed_input = (EditText) view.findViewById(R.id.ed_input);

                        String myVerification_code = ed_input.getText().toString();

                        if (myVerification_code.equals("") || myVerification_code.equals(null)) {

                            Toast.makeText(Register.this, "還沒輸入驗證碼", Toast.LENGTH_LONG).show();

                        } else {

                            if (myVerification_code.equals(correct_verification_code)) {

                                ed_register_account.setEnabled(false);

                                ed_register_password.setEnabled(true);

                                btn_reset_account.setEnabled(true);

                                btn_register.setEnabled(true);

                                btn_sent_verification_code.setEnabled(false);

                                Toast.makeText(Register.this, "驗證碼正確，請接續輸入自訂密碼以註冊" + "\n" + myVerification_code + "等於" + verification_code, Toast.LENGTH_LONG).show();

                            } else {

                                Toast.makeText(Register.this, "驗證碼錯誤，再試一次" + "\n" + myVerification_code + "不等於" + verification_code, Toast.LENGTH_LONG).show();


                            }

                        }

                    }

                });

        myAlertDialog.show();

    }

    private void setRequest_data() {

        request_data = account +

                "\n" + password +

                "\n" + date;

    }
}
