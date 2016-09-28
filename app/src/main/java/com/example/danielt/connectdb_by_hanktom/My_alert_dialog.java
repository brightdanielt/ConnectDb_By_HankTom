package com.example.danielt.connectdb_by_hanktom;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by HWT49 on 2016/8/17.
 */
public abstract class My_alert_dialog {

    private final int CHECK_INTERNET_WHEN_LOAD = 1;

    private final int CHECK_INTERNET_WHEN_SAVE = 2;

    private final int BAD_INTERNET = 3;

    private final int POST_FAILED = 4;

    private final int DECIDE_BY_MESSAGE = 5;

    private final int ASK_EDITING_FINISH = 6;

    private int loading_failed_count = 0;

    private int saving_failed_count = 0;

    private AlertDialog check_internet_dialog;

    AlertDialog data_loading_dialog;

    protected AlertDialog data_saving_dialog;

    private AlertDialog unstable_internet_dialog;

    private AlertDialog ask_editing_finish_dialog;

    private AlertDialog decide_by_message;

    private AlertDialog no_friend_dialog;

    private AlertDialog no_diary_dialog;

    Context myContext;

    String myMessage;

    int myDialog_code;

//    public My_alert_dialog() {
//    }

    public My_alert_dialog(Context myContext) {

        this.myContext = myContext;

    }

    public My_alert_dialog(Context myContext, @Nullable int dialog_code, @Nullable String myMessage) {
        this.myDialog_code = dialog_code;
        this.myContext = myContext;
        this.myMessage = myMessage;
    }

    public void show_dialog() {

        switch (myDialog_code) {

            //載入activity時檢查網路是否開啟

            case CHECK_INTERNET_WHEN_LOAD: {

                if (!isConnected()) {

                    check_internet_dialog();
                    Log.d("dialog", "no_internet");

                }

                //當網路連線成功時，執行後續動作
                else {

                    data_loading_dialog();
                    Log.d("dialog", "data_loading");

                    new Handler().postDelayed(new Runnable() {

                        @Override

                        public void run() {

                            if (action_after_dialog()) {

                                data_loading_dialog.setCancelable(true);

                                data_loading_dialog.cancel();

                                Toast.makeText(myContext, "data_loading_success", Toast.LENGTH_LONG).show();

                            } else {

                                data_loading_dialog.setCancelable(true);

                                data_loading_dialog.cancel();

                                //累加失敗次數

                                loading_failed_count++;

                                //最多容忍抓 server 資料失敗三次,超過則顯示 unstable_internet_dialog

                                if (loading_failed_count > 3) {

                                    unstable_internet_dialog();

                                } else {

                                    show_dialog();

                                }

                                Log.d("dialog", "unstable_internet");

                            }

                        }

                    }, 2000);

//                    Toast.makeText(myContext, "OK125", Toast.LENGTH_LONG).show();

                }

                break;
            }

            case CHECK_INTERNET_WHEN_SAVE: {

                if (isConnected() == false) {

                    check_internet_dialog();
                    Log.d("dialog", "no_internet");

                }

                //當網路連線成功時，執行後續動作
                else {

                    data_saving_dialog();
                    Log.d("dialog", "data_saving");

                    new Handler().postDelayed(new Runnable() {

                        @Override

                        public void run() {

                            if (action_after_dialog()) {

                                data_saving_dialog.setCancelable(true);

                                data_saving_dialog.cancel();

                                Toast.makeText(myContext, "data_saving_success", Toast.LENGTH_LONG).show();

                            } else {

                                data_saving_dialog.setCancelable(true);
//
                                data_saving_dialog.cancel();

                                //累加失敗次數

                                saving_failed_count++;

                                //最多容忍儲存資料失敗三次,超過則顯示 unstable_internet_dialog

                                if (saving_failed_count > 3) {

                                    unstable_internet_dialog();

                                } else {

                                    show_dialog();

                                }

                                Log.d("dialog", "unstable_internet");

                            }

                        }

                    }, 2000);

//                    Toast.makeText(myContext, "OK125", Toast.LENGTH_LONG).show();

                }

                break;
            }


            case ASK_EDITING_FINISH: {

                ask_editing_finish_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                        .setTitle("想問問你")

                        .setIcon(R.drawable.personal_info)

                        .setMessage("編輯完成了嗎?")

                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                                Log.d("ask_editing_finish_d", "btn_yes");

                                action_after_dialog();

//                                new My_alert_dialog(myContext, CHECK_INTERNET_WHEN_SAVE, null) {
//
//                                    @Override
//
//                                    boolean action_after_getInternet() {

                                //似乎執行不到呼叫方所實作的方法
//
//                                        return action_after_getInternet();
//
//                                    }
//                                }.check_internet();

                            }
                        })
                        .setNegativeButton("還沒", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //停在原畫面，不做動作
                                Log.d("ask_editing_finish_d", "btn_no");
                            }
                        })
                        .show();

                break;
            }

            case DECIDE_BY_MESSAGE: {

                decide_by_message = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                        .setMessage(myMessage)

                        .setCancelable(false)

                        .show();

                break;

            }

        }

    }

    protected void check_internet_dialog() {

        if (isConnected()==false) {

            check_internet_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                    .setTitle("Ops！")

                    .setIcon(R.drawable.personal_info)

                    .setMessage(R.string.no_internet)

                    .setCancelable(false)

                    .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(final DialogInterface dialog, int which) {

                            check_internet_dialog();

                            //2秒後再次檢查網路

//                                    new Handler().postDelayed(new Runnable() {
//
//                                        @Override
//
//                                        public void run() {
//
//                                            try_internet_dialog.setCancelable(true);
//
//                                            try_internet_dialog.cancel();
//
//                                            show_dialog();
//
//                                        }
//
//                                    }, 2000);

                        }

                    })

                    .show();
        } else {

            action_after_dialog();

        }

    }

    protected void data_loading_dialog() {

        data_loading_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                .setTitle("                     ")

                .setMessage(R.string.loading)

                .setCancelable(false)

                .show();

    }

    protected void data_saving_dialog() {

        data_saving_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                .setTitle("                     ")

                .setMessage(R.string.saving)

                .setCancelable(false)

                .show();

    }

    private void unstable_internet_dialog() {

        unstable_internet_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                .setMessage(R.string.unstable_internet)

                .setCancelable(false)

                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        unstable_internet_dialog.setCancelable(true);

                        unstable_internet_dialog.cancel();

                        show_dialog();

                    }
                })
                .show();

    }

    private void no_friend_dialog() {

        no_friend_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                .setTitle(R.string.oops)

                .setMessage(R.string.no_friend)

                .setCancelable(false)

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        action_after_dialog();

                    }
                })

                .show();

    }

    private void no_diary_dialog() {

        no_friend_dialog = new AlertDialog.Builder(myContext, R.style.selectorDialog)

                .setTitle(R.string.oops)

                .setMessage(R.string.no_diary)

                .setCancelable(false)

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        action_after_dialog();

                    }
                })

                .show();

    }

    //提共抽象方法，執行網路連線成功後的動作

    abstract boolean action_after_dialog();

    //檢查網路是否連線

    private boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            return true;

        }

        return false;

    }
}
