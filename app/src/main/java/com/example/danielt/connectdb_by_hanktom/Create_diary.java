package com.example.danielt.connectdb_by_hanktom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Create_diary extends AppCompatActivity {

    final String CREATE_DIARY = "http://218.161.15.149:8080/AndroidConnectDB/create_diary.php";

    Diary diary, diary_fromCalendar = new Diary(); //前者用於儲存日記內容，後者用於接收bundle

    All_diary all_diary = new All_diary();    //用於接收Simple_browse_diary所傳來的bundle

    int uId, authority, mark = 0, img_dfwidth, img_dfheigh;

    String title, date, diary_text, mood, pic = "pic", request_data;

    boolean editable = false;

    int year, month, day;

    TextView tv_date;

    EditText ed_title, ed_mood, ed_diary_text;

    ImageView imgView_choosePic, img_pic;

    ImageButton btn_edit;

    CheckBox checkMark;

    File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    int current_size, df_size;

    ScrollView scrollview_pic;

    My_alert_dialog my_alert_dialog;

    LinearLayout.LayoutParams layoutParams;

    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照

    public static final int PHOTO_REQUEST_GALLERY = 2;// 從相簿選擇

    public static final int PHOTO_REQUEST_CUT = 3;// 結果

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.alpha_0to1, R.anim.alpha_1to0);

        setContentView(R.layout.activity_create_diary);

        getDataFromBundle();

        doFindView();

        setViewEffect();

        initialization_Diary();

    }

    private void getDataFromBundle() {

        //接收開啟時來自All_diary夾帶的bundle資料

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        //接收開啟時來自Calendar夾帶的bundle資料

        uId = bundle.getInt("uId");

        authority = bundle.getInt("authority");

        date = bundle.getString("date");

        Log.i("LOG_calendar", "date in diary:" + date);

        diary_fromCalendar = (Diary) bundle.getSerializable("diary");

        all_diary = (All_diary) bundle.getSerializable("selected_diary");

    }

    private void doFindView() {

        tv_date = (TextView) findViewById(R.id.tv_date);

        ed_title = (EditText) findViewById(R.id.ed_title);

        ed_mood = (EditText) findViewById(R.id.ed_mood);

        ed_diary_text = (EditText) findViewById(R.id.ed_diary_text);

        imgView_choosePic = (ImageView) findViewById(R.id.imgView_choosePic);

        img_pic = (ImageView) findViewById(R.id.imgView_pic);

        checkMark = (CheckBox) findViewById(R.id.checkMark);

        btn_edit = (ImageButton) findViewById(R.id.btn_edit);

        scrollview_pic = (ScrollView) findViewById(R.id.scrollview_pic);

        layoutParams = (LinearLayout.LayoutParams) img_pic.getLayoutParams();

    }

    private void setViewEffect() {

        img_dfwidth = layoutParams.width;

        img_dfheigh = layoutParams.height;

        current_size = img_pic.getWidth();

        df_size = img_pic.getWidth();

        tv_date.setText(date);

        if (authority == 0) {
            btn_edit.setVisibility(View.INVISIBLE);
        }

        btn_edit.setOnClickListener(new View.OnClickListener() {

                                        @Override

                                        public void onClick(View v) {

                                            changeEditable();

                                            ed_title.requestFocus();//讓EditText獲得焦點，但是獲得焦點並不會自動彈出鍵盤

                                            //編輯文字時，自動彈出鍵盤

                                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                                        }
                                    }

        );

        checkMark.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {

                    mark = 1;

                } else {

                    mark = 0;

                }

            }

        });

        imgView_choosePic.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                ask_picDialog();

            }

        });

        img_pic.setOnTouchListener(new View.OnTouchListener() {

            float currentDistance;
            float lastDistance = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.d("onTouch", "onTouch");

                switch (event.getAction()) {

//                    case MotionEvent.ACTION_DOWN:{
//
//                        Log.d("ACTION_DOWN","ACTION_DOWN");
//
//                        break;
//                    }


                    case MotionEvent.ACTION_MOVE: {

                        Log.d("ACTION_MOVE", "ACTION_MOVE");

                        if (event.getPointerCount() >= 2) {

                            float offsetX = event.getX(0) - event.getX(1);

                            float offsetY = event.getY(0) - event.getY(1);

                            currentDistance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

                            if (lastDistance < 0) {

                                lastDistance = currentDistance;

                            } else {

                                if (currentDistance - lastDistance > 5) {

                                    Log.d("ACTION_MOVE", "放大");

                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img_pic.getLayoutParams();

                                    layoutParams.width = (int) (layoutParams.width * 1.02);

                                    layoutParams.height = (int) (layoutParams.height * 1.02);

                                    img_pic.setLayoutParams(layoutParams);

                                    lastDistance = currentDistance;

                                } else if (lastDistance - currentDistance > 5) {

                                    Log.d("ACTION_MOVE", "縮小");

                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img_pic.getLayoutParams();

                                    layoutParams.width = (int) (layoutParams.width * 0.98);

                                    layoutParams.height = (int) (layoutParams.height * 0.98);

                                    img_pic.setLayoutParams(layoutParams);

                                    lastDistance = currentDistance;

                                    lastDistance = currentDistance;

                                } else {

                                    Log.d("ACTION_MOVE", "NO!!");


                                }

                            }

                        }

                        break;

                    }

                    case MotionEvent.ACTION_UP: {

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img_pic.getLayoutParams();

                        layoutParams.width = img_dfwidth;

                        layoutParams.height = img_dfwidth;

                        img_pic.setLayoutParams(layoutParams);

                        break;

                    }


//                    case MotionEvent.ACTION_HOVER_EXIT:{
//
//                        break;
//                    }

                }
                return true;
            }
        });

    }

    private void ask_picDialog() {

        new android.app.AlertDialog.Builder(this)

                .setTitle("選擇照片來源")

                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // TODO Auto-generated method stub

                        dialog.dismiss();

                        // 调用系统的拍照功能

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // 指定调用相机拍照后照片的储存路径

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));

                        //第二個參數是 requestCode ，使用於返回後調用 onActivityResult() 的參數

                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);

                    }

                })

                .setNegativeButton("相簿", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        // TODO Auto-generated method stub

                        dialog.dismiss();

                        Intent intent = new Intent(Intent.ACTION_PICK, null);

                        //開啟手機相簿
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                    }

                }).show();

    }

    //選擇圖片或拍照後，自動調用此方法
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub

        switch (requestCode) {

            case PHOTO_REQUEST_TAKEPHOTO:

                // 创建一个以当前时间为名称的文件


                startPhotoZoom(Uri.fromFile(tempFile), 150);

                break;

            case PHOTO_REQUEST_GALLERY:

                if (data != null)

                    startPhotoZoom(data.getData(), 150);

                break;

            case PHOTO_REQUEST_CUT:

                if (data != null)

                    setPicToView(data);

                break;

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void startPhotoZoom(Uri uri, int size) {

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        // crop为true是设置在开启的intent中设置显示的view可以剪裁

        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例

        intent.putExtra("aspectX", 1);

        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高

        intent.putExtra("outputX", size);

        intent.putExtra("outputY", size);

        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);

    }

    //将进行剪裁后的图片显示到UI界面上

    private void setPicToView(Intent picdata) {

        Bundle bundle = picdata.getExtras();

        if (bundle != null) {

            Bitmap photo = bundle.getParcelable("data");

            Drawable drawable = new BitmapDrawable(photo);

            img_pic.setImageResource(0);

            img_pic.setImageDrawable(drawable);

            Toast.makeText(Create_diary.this, photo.toString(), Toast.LENGTH_SHORT).show();


        }
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void save_diary() {

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

                    diary = (Diary) connectDb.askConnect(CREATE_DIARY, request_data);

//                    final String title=diary.getTitle().toString();

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            my_alert_dialog.data_saving_dialog.setCancelable(true);

                            my_alert_dialog.data_saving_dialog.dismiss();

                            //對日記內容的應用
                            if(diary==null){

                                my_alert_dialog.check_internet_dialog();

                            }
                            else if(diary.getSuccess()==1){

                                //儲存成功

                            }
                            else{

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

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (editable == true && authority == 1 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   //確定按下退出鍵and防止重複按下退出鍵

            changeEditable();

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();

        }

        return false;

    }

    private void ask_leave_dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Create_diary.this)

                .setMessage("要儲存這篇日記嗎?")

                .setTitle("想問問你...")

                .setCancelable(false)

                .setPositiveButton("儲存", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        setRequest_data();

                        dialog.dismiss(); //dismiss為關閉dialog,Activity還會保留dialog的狀態

                        Log.d("create_diary", "request_data" + request_data);

                        my_alert_dialog=new My_alert_dialog(Create_diary.this) {
                            @Override
                            boolean action_after_dialog() {

                                my_alert_dialog.data_saving_dialog();

                                save_diary();

                                return true;
                            }
                        };

                        my_alert_dialog.check_internet_dialog();

                        editable = false;

                    }
                });

        builder.setNegativeButton("直接離開", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                finish();

            }

        });

        builder.create().show();

    }

    private void changeEditable() {

        editable = !editable;

        if (editable == false) {

            ask_leave_dialog();

            ed_title.setEnabled(false);

            ed_mood.setEnabled(false);

            ed_diary_text.setEnabled(false);

            checkMark.setEnabled(false);

            img_pic.setEnabled(false);

            imgView_choosePic.setVisibility(View.INVISIBLE);

            btn_edit.setBackgroundResource(R.drawable.btn_edit);

        } else {

            ed_title.setEnabled(true);

            ed_mood.setEnabled(true);

            ed_diary_text.setEnabled(true);

            checkMark.setEnabled(true);

            img_pic.setEnabled(true);

            imgView_choosePic.setVisibility(View.VISIBLE);

            btn_edit.setBackgroundResource(R.drawable.btn_ok);

        }
    }

    private void initialization_Diary() {

        try {

            if (diary_fromCalendar == null && all_diary == null) {

                //只需載入日期

                tv_date.setText(date);

            } else {

                if ((diary_fromCalendar != null)) {

                    //載入各項日記的紀錄

//                    tv_date.setText(diary_fromCalendar.getDate());

                    tv_date.setText(date);

                    ed_title.setText(diary_fromCalendar.getTitle());

                    ed_mood.setText(diary_fromCalendar.getMood());

                    ed_diary_text.setText(diary_fromCalendar.getDiary_text());

                    mark = diary_fromCalendar.getMark();

                    pic = diary_fromCalendar.getPic();

                }

                if ((all_diary != null)) {

                    //載入各項日記的紀錄

                    tv_date.setText(all_diary.getDate());

                    ed_title.setText(all_diary.getTitle());

                    ed_mood.setText(all_diary.getMood());

                    ed_diary_text.setText(all_diary.getDiary_text());

                    mark = all_diary.getMark();

                    pic = all_diary.getPic();

                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        if (mark == 1) {

            checkMark.setChecked(true);

        }

        if (authority < 1) {

            btn_edit.setEnabled(false);

        }

    }

    private void setRequest_data() {

        //取得日記的內容
        request_data = uId

                + "\r" + tv_date.getText().toString()

                + "\r" + ed_title.getText().toString()

                + "\r" + mark

                + "\r" + ed_mood.getText().toString()

                + "\r" + ed_diary_text.getText().toString()

                + "\r" + pic;

    }
}
