package com.example.danielt.connectdb_by_hanktom;

/**
 * Created by HWT49 on 2016/6/16.
 */

public class Transaction {

    int uId;
    String account;
    String password;

    public Transaction(){

    }

    public Transaction(int uId, String password, String account) {

        this.uId = uId;

        this.password = password;

        this.account = account;

    }

    public void setUId(int uId) {
        this.uId = uId;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUId() {
        return uId;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
