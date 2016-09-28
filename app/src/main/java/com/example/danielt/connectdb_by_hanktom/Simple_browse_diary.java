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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

public class Simple_browse_diary extends AppCompatActivity {

    DatePicker datePicker;

    ListView listView;

    final String LOAD_ALL_DIARY = "http://218.161.15.149:8080/AndroidConnectDB/load_all_diary.php";

    String request_data;

    All_diary[] diary_array;

    List<All_diary> diary_inListView;

    String[] title, diary_text, date;

    int[] img_pic_src, img_mark_int;

    int uId, authority;

    ListView.OnItemClickListener listViewListener;

//    private LinearLayout linelay;
//    private ImageSwitcher imgSwi2;

    TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_simple_browse_diary);

        getDataFromBundle();

        doFindView();

        setViewEffect();

        //載入相對uId的所有日記

        load_all_diary();

    }

    private void getDataFromBundle() {

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        //接收開啟時來自Friend_list夾帶的bundle資料


        uId = bundle.getInt("fId");

        authority = bundle.getInt("authority");

    }

    private void doFindView() {

        listView = (ListView) findViewById(R.id.listView);

        datePicker = (DatePicker) findViewById(R.id.datePicker);

    }

    private void setViewEffect() {

        listViewListener = new ListView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                All_diary selected_diary = new All_diary();

                selected_diary = diary_inListView.get(position);

                Diary diary = new Diary();

                Bundle bundle = new Bundle();

                //夾帶選重的All_diary給要開啟的Activity

                bundle.putSerializable("selected_diary", selected_diary);

                bundle.putSerializable("diary", diary);

                bundle.putInt("authority", authority);

                Intent intent = new Intent();

                intent.putExtras(bundle);

                intent.setClass(Simple_browse_diary.this, Create_diary.class);

                startActivity(intent);

            }
        };

        //取消顯示日曆部分的datePicker
//        datePicker.setCalendarViewShown(false);

        if (datePicker != null) {
            //只显示年月

            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0))

                    .getChildAt(2).setVisibility(View.GONE);

//            .getChildAt(0)
//            只显示年日
//            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
//            .getChildAt(2).setVisibility(View.GONE);
//            只显示年月
//            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
//            .getChildAt(1).setVisibility(View.GONE);
//            显示月日
//            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
//            .getChildAt(0).setVisibility(View.GONE);

        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        calendar.add(java.util.Calendar.DAY_OF_YEAR, 0);

        long maxDate = calendar.getTimeInMillis();

        //設置可以檢視與點選的日期為當日以前
        datePicker.setMaxDate(maxDate);

        int year = calendar.get(java.util.Calendar.YEAR);

        //取得年分

        int month = calendar.get(java.util.Calendar.MONTH);

        //取得月份

        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        //取得天數

        //變更日期時的動作

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year, int month, int day) {

                if (get_selected_diary(year, month + 1) == null) {

                    diary_inListView = new ArrayList<All_diary>();

                } else {

                    //將選擇的年與月當get_selected_diary的參數

                    //回傳物件為與該年月相符合的All_diary的陣列

                    diary_inListView = get_selected_diary(year, month + 1);

                }

                listView.startAnimation(AnimationUtils.loadAnimation(Simple_browse_diary.this,R.anim.alpha_0to1));
                listView.startAnimation(AnimationUtils.loadAnimation(Simple_browse_diary.this,R.anim.translate_rtol));

                //建立相對日期的adapter

                Simple_diary_Adapter simple_diary_adapter = new Simple_diary_Adapter(Simple_browse_diary.this);

                //啟用案件過濾功能

                listView.setTextFilterEnabled(true);

                //設定被選中物件的圖示

                //listView.setSelector(R.drawable.select);

                //設定被選種物件的圖示是否在物件頂端，即覆蓋下層物件的意思

                listView.setDrawSelectorOnTop(false);

                listView.setAdapter(simple_diary_adapter);

                listView.setOnItemClickListener(listViewListener);

            }

        });

    }

    public void load_all_diary() {

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

                    request_data = uId + "\n";

                    //將呼叫的PHP檔及日記內容傳給askConnect
                    //回傳為Object型態，將他轉為Diary以使用日記內容
                    //因為回傳物件是存放日記的陣列，所以轉成All_diary[]

                    diary_array = (All_diary[]) connectDb.askConnect(LOAD_ALL_DIARY, request_data);


                    //更改UI畫面的程式碼必須由UI的執行續執行

                    runOnUiThread(new Runnable() {

                        //要改變介面的程式寫在run()裡面

                        public void run() {

                            //對UI的變更，可以寫在另一個方法，呼叫使用

                            //diary_array[0]就是讀取到的第一篇日記，依此類推

                            if (diary_array[0].getSuccess() == 1) {

                                //在沒有日記的情況下，不顯示datePicker

                            } else {

                                datePicker.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder no_diary = new AlertDialog.Builder(Simple_browse_diary.this);

                                no_diary.setTitle("咦?")

                                        .setMessage("你的朋友還沒寫下任何的日記!")

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

    public List<All_diary> get_selected_diary(int year, int month) {

        List<All_diary> all_diary_list = new ArrayList<>();

        All_diary[] selected_diary = null;

        int diary_count = diary_array.length;

        int selected_diary_count = 0;

        if (diary_count < 1) {

        } else {

//            for (int index = 0; index < diary_count; index++) {
//
//                //取得指定日記的日期陣列
//                int[] date_int_array = diary_array[index].getDate_int_array();
//
//                //比對該日期與user所選是否相同
//                if (date_int_array[0] == year) {
//
//                    if (date_int_array[1] == month) {
//
//                        //計算共幾篇日記相同
//                        selected_diary_count++;
//
//                    }
//                }
//            }
            //經過上面的迴圈得知要建立多大的All_diary[]
            //建立All_diary[]物件，用於存放日期相同的All_diary

//            selected_diary = new All_diary[selected_diary_count];
//
//            int selected_diary_index = 0;

            for (int index = 0; index < diary_count; index++) {

                int[] date_int_array = diary_array[index].getDate_int_array();

                if (date_int_array[0] == year) {

                    if (date_int_array[1] == month) {

//                        selected_diary[selected_diary_index] = diary_array[index];

                        all_diary_list.add(diary_array[index]);

//                        selected_diary_index++;

                    }
                }
            }

            return all_diary_list;

        }

        return null;

    }

    class Simple_diary_Adapter extends BaseAdapter {

        private LayoutInflater myInflater;

        public Simple_diary_Adapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }

        @Override

        public int getCount() {
            return diary_inListView.size();
        }

        @Override

        public Object getItem(int position) {
            return diary_inListView.get(position);
        }

        @Override

        public long getItemId(int position) {
            return position;
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = myInflater.inflate(R.layout.simple_diary_listview, null);

            ImageView img_pic = (ImageView) convertView.findViewById(R.id.img_pic);

            ImageView img_mark = (ImageView) convertView.findViewById(R.id.img_mark);

            TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);

            TextView tv_diary_text = (TextView) convertView.findViewById(R.id.tv_diary_text);

            tv_diary_text.setMaxLines(2);

            TextView tv_date = (TextView) convertView.findViewById(R.id.tv_1);


//            img_pic.setImageResource(img_pic_src[position]);

            if (diary_inListView.get(position).getMark() == 1) {

                img_mark.setImageResource(R.drawable.mark_star);

            }

            tv_title.setText(diary_inListView.get(position).getTitle());

            tv_diary_text.setText(diary_inListView.get(position).getDiary_text());

            tv_date.setText(diary_inListView.get(position).getDate());

            return convertView;

        }
    }

}

class All_diary implements Serializable {

    String describe, date, title = "no_title", mood, diary_text = "no_record_from_All_diary", pic = "pic";

    int success, mark = 0;

    int[] date_int_array;

    All_diary[] diary_array;

    public All_diary() {

    }

    //解析PHP傳回的response以建立diary物件

    public All_diary(String response) {

        try {

            JSONArray array = new JSONArray(response);

            if (array.length() == 1) {

                //建立存放Diary物件的陣列

                diary_array = new All_diary[1];

            } else {

                //建立diary陣列

                //-1是因為第一個陣列是message,其他的才是diary

                diary_array = new All_diary[(array.length() - 1)];

            }

            //初始化

            diary_array[0] = new All_diary();

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                //該response存了至少一個陣列，第一個是message，有關傳送成功失敗的訊息

                //所以不論成功與否至少要建一個All_diary提共查詢連線訊息

                if (i == 0) {

                    int success = obj.getInt("success");

                    String describe = obj.getString("describe");

                    diary_array[0].setSuccess(success);

                    diary_array[0].setDescribe(describe);

                    Log.d("parseJSON", success + "/" + describe);

                } else {

                    //第二個是回傳新增的資料

                    int uId = obj.getInt("uId");

                    String date = obj.getString("date");

                    String title = obj.getString("title");

                    String mood = obj.getString("mood");

                    String diary_text = obj.getString("diary_text");

                    String pic = obj.getString("pic");

                    int mark = obj.getInt("mark");

                    String[] date_string_array = date.split("-");

                    int[] date_int_array = new int[date_string_array.length];

                    //將日期字串轉為INT存入陣列

                    for (int index = 0; index < date_string_array.length; index++) {

                        int num = Integer.valueOf(date_string_array[index]);

                        date_int_array[index] = num;

                    }
                    Log.d("parseJSON", "date:" + date + "/" + "title:" + title + "mood:" + mood + "diary_text:" + diary_text + "pic:" + pic + "mark:" + mark);

                    if (i != 1) {

                        diary_array[i - 1] = new All_diary();

                    } else {

                    }

                    diary_array[i - 1].setDate(date);

                    diary_array[i - 1].setTitle(title);

                    diary_array[i - 1].setMood(mood);

                    diary_array[i - 1].setDiary_text(diary_text);

                    diary_array[i - 1].setPic(pic);

                    diary_array[i - 1].setMark(mark);

                    diary_array[i - 1].setDate_int_array(date_int_array);

//                    Log.d("parseJSON", uId + "/" + date + "/" + title+"/" +mood+"/" +diary_text+"/" +pic+"/" +mark);
                }

            }

        } catch (JSONException e) {

            Log.d("parseJSON", "JSONArray error");

            e.printStackTrace();

        }
    }


    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public void setDiary_text(String diary_text) {
        this.diary_text = diary_text;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setDate_int_array(int[] date_int_array) {
        this.date_int_array = date_int_array;
    }

    public String getDescribe() {
        return describe;
    }

    public int getSuccess() {
        return success;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getMood() {
        return mood;
    }

    public String getDiary_text() {
        return diary_text;
    }

    public String getPic() {
        return pic;
    }

    public int getMark() {
        return mark;
    }

    public int[] getDate_int_array() {
        return date_int_array;
    }

    public void setDiary_array(All_diary[] diary_array) {
        this.diary_array = diary_array;
    }

    public All_diary[] getDiary_array() {
        return diary_array;
    }
}


