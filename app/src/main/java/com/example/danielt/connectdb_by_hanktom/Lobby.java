package com.example.danielt.connectdb_by_hanktom;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Lobby extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int uId;

    private Login myLogin;

    private TextView tv_account;

    private DrawerLayout drawer;

    private NavigationView navigationView;

    Intent sent_intent = new Intent(),get_intent;

    Bundle sent_bundle = new Bundle(),get_bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

        setContentView(R.layout.activity_loby);

        getDataFromBundle();

        doFindView();

        setViewEffect();

    }

    private void doFindView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//
//            public void onClick(View view) {
//
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(

                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);

        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);


        //取消drawer上icon的預設屬性
        navigationView.setItemIconTintList(null);


        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setViewEffect(){

        View header=navigationView.getHeaderView(0);


        drawer.setDrawerShadow(R.drawable.no_shadow, GravityCompat.START);    // drawer 陰影

        tv_account= (TextView) header.findViewById(R.id.tv_account);

//        Toast.makeText(Lobby.this,get_bundle.getString("account"),Toast.LENGTH_LONG).show();

        tv_account.setText(get_bundle.getString("account"));

        tv_account.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                ask_input_verification_code_to_logout();

            }

        });

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        sent_bundle.putInt("uId", uId);

        sent_intent.putExtras(sent_bundle);

        if (id == R.id.write_diary) {

            sent_intent.setClass(Lobby.this, Calendar.class);

            startActivity(sent_intent);

        } else if (id == R.id.personal_info) {

            sent_bundle.putInt("uId", uId);

            sent_bundle.putInt("fId",uId);

            sent_intent.putExtras(sent_bundle);

            sent_intent.setClass(Lobby.this,Load_friend_info.class);

            startActivity(sent_intent);

        } else if (id == R.id.news) {


        } else if (id == R.id.friendlist) {

            showMyDialog(R.id.friendlist);

        } else if (id == R.id.friend_invitation) {

            showMyDialog(R.id.friend_invitation);

        } else if (id == R.id.tool) {


        } else if (id == R.id.developer) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;

    }

    private void showMyDialog(int itemId) {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(Lobby.this);

        if (itemId == R.id.friendlist) {

            myDialog.setIcon(R.drawable.friendlist)

                    .setTitle(R.string.friend)

                    .setMessage(R.string.choose_function)

                    .setPositiveButton(R.string.check_friend_diary, new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            sent_bundle.putString("toWhichActivity", "Simple_browse_diary");

                            sent_intent.putExtras(sent_bundle);

                            sent_intent.setClass(Lobby.this, Load_friend_list.class);

                            startActivity(sent_intent);

                        }
                    })

                    .setNegativeButton(R.string.check_friend_info, new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            sent_bundle.putString("toWhichActivity", "Load_friend_info");

                            sent_intent.putExtras(sent_bundle);

                            sent_intent.setClass(Lobby.this, Load_friend_list.class);

                            startActivity(sent_intent);

                        }
                    });
        }


        if (itemId == R.id.friend_invitation) {

            myDialog.setIcon(R.drawable.friend_invitation)

                    .setTitle(R.string.friend_invitation)

                    .setMessage(R.string.choose_function)

                    .setPositiveButton(R.string.sent_friend_invitation, new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            sent_intent.setClass(Lobby.this, Create_friend_invitation.class);

                            startActivity(sent_intent);

                            Toast.makeText(Lobby.this, R.string.sent_friend_invitation, Toast.LENGTH_SHORT).show();

                        }
                    })

                    .setNegativeButton(R.string.check_friend_invitation, new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            sent_intent.setClass(Lobby.this, Check_friend_invitation.class);

                            startActivity(sent_intent);

                        }
                    });
        }

        myDialog.show();

    }

    private void getDataFromBundle() {

        get_intent = getIntent();

        get_bundle = get_intent.getExtras();

        myLogin = (Login) get_bundle.getSerializable("myLogin");

        uId=myLogin.getuId();

    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   //確定按下退出鍵and防止重複按下退出鍵
//
//            ask_leave_dialog();
//
//        } else {
//
//            finish();
//
//        }
//
//        return false;
//
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   //確定按下退出鍵and防止重複按下退出鍵

            ask_leave_dialog();

        } else {



        }

        return false;
    }

    private void ask_leave_dialog() {

        AlertDialog.Builder save_info_dialog = new AlertDialog.Builder(Lobby.this);

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

    private int generate_verification_code(){

        Random rd_verification_code = new Random();

        final int correct_verification_code = 1 + rd_verification_code.nextInt(10000);

        return correct_verification_code;
    }

    private void ask_input_verification_code_to_logout() {

        Random rd_verification_code = new Random();

        final int correct_verification_code = 1 + rd_verification_code.nextInt(10000);

        LayoutInflater layoutInflater = LayoutInflater.from(Lobby.this);

        final View view = layoutInflater.inflate(R.layout.my_alert_dialog, null);

        android.app.AlertDialog.Builder myAlertDialog = new android.app.AlertDialog.Builder(Lobby.this);

        myAlertDialog.setTitle("登出帳號:  "+"\n"+get_bundle.getString("account"))

                .setView(view)

                .setMessage("請注意，登出後，下次開啟MyDiary必續輸入帳密以登入"+"\n"+"輸入以下驗證碼以登出"+"\n"+correct_verification_code)

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        EditText ed_input = (EditText) view.findViewById(R.id.ed_input);

                        String myVerification_code = ed_input.getText().toString();

                        if (myVerification_code.equals("") || myVerification_code.equals(null)) {

                            Toast.makeText(Lobby.this, "還沒輸入驗證碼", Toast.LENGTH_LONG).show();

                        } else {

                            if (myVerification_code.equals(String.valueOf(correct_verification_code))) {

                                removeLoginInfo();
                                finish();

                            } else {

                                Toast.makeText(Lobby.this, "驗證碼錯誤" + "\n" + myVerification_code + "不等於" + correct_verification_code,
                                        Toast.LENGTH_LONG).show();

                            }

                        }

                    }

                });

        myAlertDialog.show();

    }

    private void removeLoginInfo() {

        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.commit();

    }

}
