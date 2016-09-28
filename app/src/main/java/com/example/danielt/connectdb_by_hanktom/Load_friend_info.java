package com.example.danielt.connectdb_by_hanktom;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Load_friend_info extends AppCompatActivity implements UploadUtil.OnUploadProcessListener {

    private EditText ed_full_name, ed_phone_number, ed_address, ed_personal_message;

    private TextView tv_id, tv_birth_date;

    private Spinner spinner_constellation;

    private ImageView edit_btn, img_photo_sticker;

    private DatePickerDialog datePickerDialog;

    private boolean data_saving;

    private Personal_info personal_info = new Personal_info();

    Message message = new Message();

    //指定package來源,因為Calendar不同來源有同樣名稱

    private java.util.Calendar mCalendar = java.util.Calendar.getInstance();

//    private ProgressBar progressBar;

    ProgressDialog progressDialog;

    private String request_data, target_url, picPath;

    private DownLoadWebPic loadPic;

    private Handler pic_Handler;

    private final String fileType = ".jpg";

    private final String folder = "photo_sticker/";

    private final static String url = "http://218.161.15.149:8080/AndroidConnectDB/";

    private int uId, fId, authority, edit_mode = 0, my_constellation;

    final String LOAD_PERSONAL_INFO = "http://218.161.15.149:8080/AndroidConnectDB/load_personal_info.php";

    final String SAVE_PERSONAL_INFO = "http://218.161.15.149:8080/AndroidConnectDB/save_personal_info.php";

    private static String requestURL = "http://218.161.15.149:8080/AndroidConnectDB/UploadToserver.php";

    private final int CHECK_INTERNET_WHEN_SAVE = 2;

    private final int ASK_EDITING_FINISH = 6;

    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_PIC = 1;

    protected static final int TO_UPLOAD_INFO = -1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;

    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照

    public static final int PHOTO_REQUEST_GALLERY = 2;// 從相簿選擇

    public static final int PHOTO_REQUEST_CUT = 3;// 結果

    private My_alert_dialog loading_dialog, saving_dialog;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.alpha_0to1, R.anim.alpha_1to0);

        setContentView(R.layout.activity_load_friend_info);

        getDataFromBundle();

        doFindView();

        distinguish_authority();

        setViewEffect();

        loading_dialog = new My_alert_dialog(Load_friend_info.this) {
            @Override
            boolean action_after_dialog() {

                load_photo_sticker();

                load_personal_info();

                return true;

            }
        };

        loading_dialog.check_internet_dialog();

    }

//    /**
//     * @description 計算圖片的壓縮比率
//     *
//     * @param options 參數
//     * @param reqWidth 目標的寬度
//     * @param reqHeight 目標的高度
//     * @return
//     */
//    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // 源圖片的高度和寬度
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }

//    /**
//     * @description 通過傳入的bitmap，進行壓縮，得到符合標準的bitmap
//     *
//     * @param src
//     * @param dstWidth
//     * @param dstHeight
//     * @return
//     */
//    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
//        // 如果是放大圖片，filter決定是否平滑，如果是縮小圖片，filter無影響，我們這裡是縮小圖片，所以直接設置為false
//        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
//        if (src != dst) { // 如果沒有縮放，那麼不回收
//            src.recycle(); // 釋放Bitmap的native像素數組
//        }
//        return dst;
//    }
//
//    /**
//     * @description 從Resources中載入圖片
//     *
//     * @param res
//     * @param resId
//     * @param reqWidth
//     * @param reqHeight
//     * @return
//     */
//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // 設置成了true,不占用記憶體，只獲取bitmap寬高
//        BitmapFactory.decodeResource(res, resId, options); // 讀取圖片長寬，目的是得到圖片的寬高
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 調用上面定義的方法計算inSampleSize值
//        // 使用獲取到的inSampleSize值再次解析圖片
//        options.inJustDecodeBounds = false;
//        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 載入一個稍大的縮略圖
//        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 通過得到的bitmap，進一步得到目標大小的縮略圖
//    }
//
//    /**
//     * @description 從SD卡上載入圖片
//     *
//     * @param pathName
//     * @param reqWidth
//     * @param reqHeight
//     * @return
//     */
//    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(pathName, options);
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inJustDecodeBounds = false;
//        Bitmap src = BitmapFactory.decodeFile(pathName, options);
//        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
//    }
//    public void loadBitmap(boolean exactable) {
//        int bmSize = 0;
//        Bitmap bm = null;
//        if (exactable) {
//            // 通過工具類來產生一個符合ImageView的縮略圖，因為ImageView的大小是50x50，所以這裡得到的縮略圖也應該是一樣大小的
//            bm = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.saber, iv.getWidth(), iv.getHeight());
//        } else {
//            // 直接載入原圖
//            bm = BitmapFactory.decodeResource(getResources(), R.drawable.saber);
//        }
//        iv.setImageBitmap(bm);
//        bmSize += bm.getByteCount(); // 得到bitmap的大小
//        int kb = bmSize / 1024;
//        int mb = kb / 1024;
//        Toast.makeText(this, "bitmap size = " + mb + "MB" + kb + "KB", Toast.LENGTH_LONG).show();
//    }
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            Log.e("uri", uri.toString());
//            ContentResolver cr = this.getContentResolver();
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//
//                /* 將Bitmap設定到ImageView */
//                img_photo_sticker.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                Log.e("Exception", e.getMessage(),e);
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void getDataFromBundle() {

        Intent intent = this.getIntent();

        Bundle bundle = intent.getExtras();

        //接收開啟時夾帶的bundle資料

        uId = bundle.getInt("uId");

        fId = bundle.getInt("fId");

        Log.d("getDataFromBundle.fId", fId + "");

    }

    private void doFindView() {

        edit_btn = (ImageView) findViewById(R.id.edit_btn);

        img_photo_sticker = (ImageView) findViewById(R.id.img_photo_sticker);

        tv_id = (TextView) findViewById(R.id.tv_uId);

        ed_full_name = (EditText) findViewById(R.id.ed_full_name);

        spinner_constellation = (Spinner) findViewById(R.id.spinner_constellation);

        ed_phone_number = (EditText) findViewById(R.id.ed_phone_number);

        ed_address = (EditText) findViewById(R.id.ed_address);

        tv_birth_date = (TextView) findViewById(R.id.tv_birth_date);

        ed_personal_message = (EditText) findViewById(R.id.ed_personal_message);

//        progressBar= (ProgressBar) findViewById(R.id.progressBar);

    }

    private void setViewEffect() {

        progressDialog = new ProgressDialog(this);

        spinner_constellation.setEnabled(false);

        img_photo_sticker.setEnabled(false);

        if (authority == 0) {   //不是自己的個人資訊

            edit_btn.setVisibility(View.INVISIBLE); //去掉編輯按鈕

        } else {

            edit_btn.setEnabled(true);

            edit_btn.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    if (edit_mode == 0) {

                        edit_mode = 1;

                        edit_btn.setImageResource(R.drawable.ok);

                        img_photo_sticker.setEnabled(true);

                        ed_full_name.setEnabled(true);

                        spinner_constellation.setEnabled(true);

                        ed_phone_number.setEnabled(true);

                        ed_address.setEnabled(true);

                        tv_birth_date.setEnabled(true);

                        ed_personal_message.setEnabled(true);

                    } else {

                        leave_and_save_dialog();

                    }

                }
            });
        }

        ArrayAdapter<String> adapter_constellation = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.constellation));

        adapter_constellation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_constellation.setAdapter(adapter_constellation);

        Spinner.OnItemSelectedListener sp_constellation_listener = new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                my_constellation = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner_constellation.setOnItemSelectedListener(sp_constellation_listener);

        img_photo_sticker.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

//                Intent intent = new Intent();
//                /* 開启Pictures畫面Type設定为image */
//
//                intent.setType("image/*");
//                /* 使用Intent.ACTION_GET_CONTENT這個Action */
//
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                /* 取得相片後返回本畫面 */
//                startActivityForResult(intent, 1);
            }

        });


        tv_birth_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(Load_friend_info.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {

                        mCalendar.set(year, month, day);//將選擇的日期傳給mCalendar

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        tv_birth_date.setText(format.format(mCalendar.getTime()));

                    }

                }, mCalendar.get(java.util.Calendar.YEAR), mCalendar.get(java.util.Calendar.MONTH), mCalendar.get(java.util.Calendar.DAY_OF_MONTH));


                Calendar calendar = Calendar.getInstance();

                calendar.add(Calendar.DAY_OF_YEAR, 0);

                long maxDate = calendar.getTimeInMillis();

                //設置可以檢視與點選的日期為當日以前
                datePickerDialog.getDatePicker().setMaxDate(maxDate);

                datePickerDialog.show();

            }

        });


        img_photo_sticker.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                showDialog();

            }

        });

    }

    private void distinguish_authority() {

        if (uId == fId) {

            authority = 1;

        } else {

            authority = 0;

        }
    }

    //這個方法不用override就可以使用!?
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (edit_mode == 1 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   //確定按下退出鍵and防止重複按下退出鍵

            leave_and_save_dialog();

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {    //注意 softKeyboard 的刪除鍵也會觸發 onKeyDown ，只有按下返回鍵才退出

            finish();

        }

        return false;

    }

    private void leave_and_save_dialog() {

        My_alert_dialog ask_editing_dialog = new My_alert_dialog(Load_friend_info.this, ASK_EDITING_FINISH, null) {
            @Override
            boolean action_after_dialog() {

                edit_mode = 0;

                edit_btn.setImageResource(R.drawable.pen);

                img_photo_sticker.setEnabled(false);

                ed_full_name.setEnabled(false);

                spinner_constellation.setEnabled(false);

                ed_phone_number.setEnabled(false);

                ed_address.setEnabled(false);

                tv_birth_date.setEnabled(false);

                ed_personal_message.setEnabled(false);

                target_url = SAVE_PERSONAL_INFO;

                setRequest_data();

                saving_dialog = new My_alert_dialog(Load_friend_info.this) {
                    @Override
                    boolean action_after_dialog() {

                        save_personal_info();

                        return true;

                    }
                };

                saving_dialog.check_internet_dialog();

                return true;
            }
        };

        ask_editing_dialog.show_dialog();

    }

    public void load_personal_info() {

        loading_dialog.data_loading_dialog();

        target_url = LOAD_PERSONAL_INFO;

        setRequest_data();

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

                    personal_info = (Personal_info) connectDb.askConnect(LOAD_PERSONAL_INFO, request_data);

                    Log.d("Load_personal_info", "3");

//                    final String title=diary.getTitle().toString();

                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            loading_dialog.data_loading_dialog.setCancelable(true);

                            loading_dialog.data_loading_dialog.cancel();

                            if(personal_info==null){

                                loading_dialog.check_internet_dialog();

                            }
                            else if (personal_info.getSuccess() == 1) {

                                loading_dialog.data_loading_dialog.setCancelable(true);

                                loading_dialog.data_loading_dialog.cancel();

                                //對日記內容的應用

                                tv_id.setText(String.valueOf(personal_info.getuId()));

                                ed_full_name.setText(personal_info.getFull_name());

                                spinner_constellation.setSelection(personal_info.getConstellation());

                                ed_phone_number.setText(personal_info.getPhone_number());

                                ed_address.setText(personal_info.getAddress());

                                tv_birth_date.setText(personal_info.getBirth_date());

                                ed_personal_message.setText(personal_info.getPersonal_message());


                            } else {

                                loading_dialog.check_internet_dialog();

                            }


                        }

                    });

                } catch (Exception e) {

                    //這裡還必須修改，發生預期以外的錯誤時，應回到Lobby.class

                    finish();

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
//        }
    }

    private void load_photo_sticker() {

        loadPic = new DownLoadWebPic();

        pic_Handler = new Handler() {

            @Override
            public void handleMessage(android.os.Message msg) {

                switch (msg.what) {

                    case 1:

                        img_photo_sticker.setImageBitmap(loadPic.getImg());

                        break;

                    case 0:
                        break;

                }

                super.handleMessage(msg);
            }
        };

        final String URL = url + folder + fId + fileType;

        loadPic.handleWebPic(URL, pic_Handler);

    }

    private void save_personal_info() {

        saving_dialog.data_saving_dialog();

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

                    message = (Message) connectDb.askConnect(SAVE_PERSONAL_INFO, request_data);

//                    final String title=diary.getTitle().toString();
                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            saving_dialog.data_saving_dialog.setCancelable(true);
                            saving_dialog.data_saving_dialog.cancel();

                            if (message == null) {

                                saving_dialog.check_internet_dialog();

                            } else if (message.getSuccess() == 1) {

                                saving_dialog.data_saving_dialog.setCancelable(true);

                                saving_dialog.data_saving_dialog.cancel();

                            } else {

                                saving_dialog.check_internet_dialog();

                            }

                        }
                    });

                } catch (Exception e) {

                    finish();

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

    //設定要傳給PHP的資料
    public void setRequest_data() {

        switch (target_url) {

            case LOAD_PERSONAL_INFO: {

                request_data = fId + "";  //取得要載入個人資訊的ID

                Log.d("request_data_load", request_data);

                break;

            }

            case SAVE_PERSONAL_INFO: {

                if (ed_personal_message.getText().toString() == null) {

                    ed_personal_message.setText(R.string.no_content);

                }

                request_data = fId +

                        "\r" + ed_full_name.getText() +

                        "\r" + my_constellation + " " +

                        "\r" + ed_phone_number.getText() + " " +

                        "\r" + ed_address.getText() + " " +

                        "\r" + tv_birth_date.getText() +

                        "\r" + ed_personal_message.getText() + " ";

                Log.d("request_data_save", request_data);

                break;

            }


        }
    }

    // 创建一个以当前时间为名称的文件

    File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    //提示对话框方法

    public void showDialog() {

        new android.app.AlertDialog.Builder(this)

                .setTitle("設定大頭貼")

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

//                        Intent intent = new Intent(Intent.ACTION_PICK, null);
//
//                        //開啟手機相簿
//                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//
//                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                        Intent intent = new Intent();

                        intent.setType("image/*");

                        intent.setAction(Intent.ACTION_GET_CONTENT);

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

                startPhotoZoom(Uri.fromFile(tempFile), 250);

                break;

            case PHOTO_REQUEST_GALLERY:

                if (data != null) {

                    Uri photoUri = data.getData();
//
                    String[] pojo = {MediaStore.Images.Media.DATA};
//
//                    Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
//
//                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
//
//                    cursor.moveToFirst();
//
//                    picPath = cursor.getString(columnIndex);    //已取得圖片路徑
//
//                    cursor.close();
//
//                    Log.i("PicPath", "截圖前的圖片路徑=" + picPath);

                    startPhotoZoom(data.getData(), 150);
                }
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

//        intent.setType("image/*");

//        intent.setAction(Intent.ACTION_GET_CONTENT);

        // crop 為true,即可在開啟後的intent中，設定顯示的View可剪裁

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

        /*
    * 取得圖片路徑的關鍵步驟
    *
    * 1.    Override onActivityResult,取得 intent
    *
    * 2.    Uri photoUri=intent.getData();
    *
    * 3.    String[] pojo = {MediaStore.Images.Media.DATA};
    *
    *       Cursor cursor=managedQuery (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
    *
    * 4.    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);

            cursor.moveToFirst();

/*
            picPath = cursor.getString(columnIndex);    //已取得圖片路徑

            cursor.close();
*/




/*    * 1.選擇完圖片後返回 intent
    *
    * 2.返回的 intent 再用於開啟截圖 Activity
    *
    * 3.截圖後返回 intent
    *
    * 4.Bundle bundle = intent.getExtras();

        if (bundle != null) {

            Bitmap photo = bundle.getParcelable("data");

        }
    *
    * 5.saveBitmap2file(photo,filename);
    *
    * 6.toUploadFile();
            uploadUtil.uploadFile(uId+"",fileName, fileKey, requestURL, params);  第二個參數丟File,不用丟filePath再轉File*/


    //將剪裁後的圖片顯示於 ImageView

    private void setPicToView(Intent picData) {

        //......


        Bundle bundle = picData.getExtras();

        if (bundle != null) {

            Bitmap photo = bundle.getParcelable("data");

            if (saveBitmap2file(photo, "123456")) {

                Log.d("123456", "123456");

            }

            Drawable drawable = new BitmapDrawable(photo);

            img_photo_sticker.setImageResource(0);

            img_photo_sticker.setImageDrawable(drawable);

            //要求上傳圖片
            handler.sendEmptyMessage(TO_UPLOAD_PIC);

//            Toast.makeText(Load_friend_info.this, photo.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {

        Date date = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");

        return dateFormat.format(date) + ".jpg";

    }

    File fileName;

    public boolean saveBitmap2file(Bitmap bmp, String filename) {

        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;

        int quality = 100;

        OutputStream stream = null;

        try {

            //圖片完整檔名

            fileName = new File(Environment.getExternalStorageDirectory(), uId + ".jpeg");

            stream = new FileOutputStream(fileName);

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

        return bmp.compress(format, quality, stream);
    }

    private void toUploadFile() {

//        uploadImageResult.setText("正在上传中...");
//
        progressDialog.setMessage("正在上傳文件...");

        progressDialog.show();

        String fileKey = "pic";

        UploadUtil uploadUtil = UploadUtil.getInstance();

        uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

        Map<String, String> params = new HashMap<String, String>();

        params.put("orderId", "11111");

        uploadUtil.uploadFile(uId + "", fileName, fileKey, requestURL, params);

    }


    @Override
    public void onUploadDone(int responseCode, String message) {

        progressDialog.dismiss();

        android.os.Message msg = android.os.Message.obtain();

        msg.what = UPLOAD_FILE_DONE;

        msg.arg1 = responseCode;

        msg.obj = message;

        handler.sendMessage(msg);

    }

    @Override
    public void onUploadProcess(int uploadSize) {

        android.os.Message msg = android.os.Message.obtain();

        msg.what = UPLOAD_IN_PROCESS;

        msg.arg1 = uploadSize;

        handler.sendMessage(msg);

    }

    @Override
    public void initUpload(int fileSize) {

        android.os.Message msg = android.os.Message.obtain();

        msg.what = UPLOAD_INIT_PROCESS;

        msg.arg1 = fileSize;

        handler.sendMessage(msg);

    }

    private Handler handler = new Handler() {

        @Override

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {

                case TO_UPLOAD_PIC:

                    toUploadFile();

                    break;

                case UPLOAD_INIT_PROCESS:

//                    progressBar.setMax(msg.arg1);

                    break;

                case UPLOAD_IN_PROCESS:

//                    progressBar.setProgress(msg.arg1);

                    break;

                case UPLOAD_FILE_DONE:

                    String result = "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj + "\n耗时：" + UploadUtil.getRequestTime() + "秒";

//                    uploadImageResult.setText(result);

                    break;

                default:

                    break;

            }

            super.handleMessage(msg);

        }

    };

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


}


class Personal_info {

    private int success = 0, uId, constellation;

    private String describe, full_name, phone_number, address, birth_date, personal_message;

    public Personal_info() {

    }

    public Personal_info(String response) {

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

                    String full_name = obj.getString("full_name");

                    int constellation = obj.getInt("constellation");

                    String phone_number = obj.getString("phone_number");

                    String address = obj.getString("address");

                    String birth_date = obj.getString("birth_date");

                    String personal_message = obj.getString("personal_message");

                    //set完各項變數後，Personal_info 物件的資料就齊全了，可以提共個別取得 Personal_info 的資料

                    setuId(uId);

                    setFull_name(full_name);

                    setConstellation(constellation);

                    setPhone_number(phone_number);

                    setAddress(address);

                    setBirth_date(birth_date);

                    setPersonal_message(personal_message);

                    Log.d("parseJSON", uId + "/" + full_name + "/" + constellation + "/" + phone_number +
                            "/" + address + "/" + birth_date + "/" + personal_message);

                }
            }

        } catch (JSONException e) {

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

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setPersonal_message(String personal_message) {
        this.personal_message = personal_message;
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

    public String getFull_name() {
        return full_name;
    }

    public int getConstellation() {
        return constellation;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getAddress() {
        return address;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getPersonal_message() {
        return personal_message;
    }
}


