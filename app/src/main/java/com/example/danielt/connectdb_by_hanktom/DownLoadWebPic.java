package com.example.danielt.connectdb_by_hanktom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HWT49 on 2016/8/16.
 */
public class DownLoadWebPic {

    Bitmap bmp = null;

    public synchronized Bitmap getUrlPic(String url) {

        Bitmap webImg = null;

        try {
            URL imgUrl = new URL(url);
            HttpURLConnection httpURLConnection
                    = (HttpURLConnection) imgUrl.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            int length = (int) httpURLConnection.getContentLength();
            int tmpLength = 512;
            int readLen = 0,desPos = 0;
            byte[] img = new byte[length];
            byte[] tmp = new byte[tmpLength];
            if (length != -1) {
                while ((readLen = inputStream.read(tmp)) > 0) {
                    System.arraycopy(tmp, 0, img, desPos, readLen);
                    desPos += readLen;
                }
                webImg = BitmapFactory.decodeByteArray(img, 0,img.length);
                if(desPos != length){
                    throw new IOException("Only read" + desPos +"bytes");
                }
            }
            httpURLConnection.disconnect();
        }
        catch (IOException e) {
            Log.e("IOException",e.toString());
        }
        return webImg;
    }

    public void handleWebPic(final String url, final Handler handler){
        new Thread(new Runnable(){
            @Override
            public void run() {

                if(getUrlPic(url)!=null){

                    bmp = getUrlPic(url);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }
                else {

                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);

                }

            }
        }).start();
    }

    public Bitmap getImg(){
        return bmp;
    }
    
}
