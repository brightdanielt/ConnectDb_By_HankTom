package com.example.danielt.connectdb_by_hanktom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HWT49 on 2016/7/20.
 */
public class GetSdPhoto extends Activity {

    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照

    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    public static final int PHOTO_REQUEST_CUT = 3;// 结果

    Context myContext;

    ImageView photo_sticker;



    public GetSdPhoto(){

    }

    public GetSdPhoto(Context fromContext){

        myContext=fromContext;

    }

    // 创建一个以当前时间为名称的文件

    File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());

    //提示对话框方法

    public void showDialog() {

        new AlertDialog.Builder(myContext)

                .setTitle("头像设置")

                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // TODO Auto-generated method stub

                        dialog.dismiss();

                        // 调用系统的拍照功能

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // 指定调用相机拍照后照片的储存路径

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));

                        //第二個參數是requestCode，使用於返回後調用onActivityResult()的參數

                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);

                    }

                })

                .setNegativeButton("相册", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        // TODO Auto-generated method stub

                        dialog.dismiss();

                        Intent intent = new Intent(Intent.ACTION_PICK, null);

                        //開啟手機相簿
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

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

            photo_sticker.setBackgroundDrawable(drawable);

        }
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }


}
