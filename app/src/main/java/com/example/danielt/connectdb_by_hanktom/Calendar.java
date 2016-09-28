package com.example.danielt.connectdb_by_hanktom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

public class Calendar extends AppCompatActivity {

    String date, theYear, theMonth, theDay;

    Diary diary = null;

    final String LOAD_DIARY = "http://218.161.15.149:8080/AndroidConnectDB/load_diary.php";

    String request_data;

    int uId;

    Login myLogin;

    CalendarView calendarView;

    Intent intent;

    Bundle bundle;

//    ProgressDialog pd;

    My_alert_dialog my_alert_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.alpha_0to1, R.anim.alpha_1to0);

        setContentView(R.layout.activity_calendar);

        getDataFromBundle();

        doFindView();

        setViewEffect();

    }

    private void doFindView() {

        calendarView = (CalendarView) findViewById(R.id.calendarView);

    }

    private void setViewEffect() {

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        calendar.add(java.util.Calendar.DAY_OF_YEAR, 0);

        long maxDate = calendar.getTimeInMillis();

        //設置可以檢視與點選的日期為當日以前

        calendarView.setMaxDate(maxDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                //月份記得加一，因為一月是從0開始算

//                theYear=String.valueOf(year);
//                theMonth=String.valueOf(month+1);
//                theDay=String.valueOf(dayOfMonth);

                //建立日期:年-月-日,因為DB中資料欄位也是以此格式儲存
                //小心轉成字串時，數字1是存成01，會造成事後字串比對錯誤

                date = year + "-" + (month + 1) + "-" + dayOfMonth;

                Log.i("LOG_calendar", "date:" + date);

                //取得uId和date
                setRequest_data();

                my_alert_dialog = new My_alert_dialog(Calendar.this) {
                    @Override
                    boolean action_after_dialog() {

                        load_diary();

                        return true;
                    }
                };

                my_alert_dialog.check_internet_dialog();


            }
        });

    }

    private void getDataFromBundle() {

        intent = getIntent();

        bundle = intent.getExtras();

        uId = bundle.getInt("uId");

    }

    public void load_diary() {

//        pd = new ProgressDialog(Calendar.this);
//
//        pd.setMessage("Loading");
//
//        pd.show();
//
//        pd.setCancelable(false);
//
        my_alert_dialog.data_loading_dialog();

        //在新的執行緒中才能使用回傳的資料對UI做更新

        new Thread(new Runnable() {

            @Override

            public void run() {

                Looper.prepare();

                // TODO Auto-generated method stub

                //連線的程式碼要放在try catch內，才不會發生error

                try {

                    Log.d("load_diary", "0");

                    ConnectDb connectDb = new ConnectDb();

                    //將呼叫的PHP檔及日記內容傳給askConnect

                    //回傳為Object型態，將他轉為Diary以使用日記內容

                    diary = (Diary) connectDb.askConnect(LOAD_DIARY, request_data);

//                    if(diary.getDiary_text()=="no_record"){
//
//                        diary=null;
//
//                    }

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            my_alert_dialog.data_loading_dialog.setCancelable(true);

                            my_alert_dialog.data_loading_dialog.cancel();

                            if (diary == null) {

                                my_alert_dialog.check_internet_dialog();

                            } else if (diary != null) {

//                                pd.dismiss();

                                Bundle bundle = new Bundle();

                                //夾帶資料給要開啟的Activity

                                bundle.putSerializable("diary", diary);

                                bundle.putInt("uId", uId);

                                bundle.putString("date", date);

                                bundle.putInt("authority", 1);

                                Intent intent = new Intent();

                                intent.putExtras(bundle);

                                intent.setClass(Calendar.this, Create_diary.class);

                                startActivity(intent);

                                //對日記內容的應用
//                            ed_title.setText("get"+title);

                            } else {
                                my_alert_dialog.check_internet_dialog();
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

    public void setRequest_data() {

        //取得日記的內容
        request_data = uId

                + "\n" + date;

        Log.d("load_diary", request_data);
    }
}
