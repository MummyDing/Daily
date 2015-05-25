package com.example.daily;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MummyDing on 2015/5/20.
 */
public class User extends DataSupport {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<Daily> getDailylist() {
        return dailylist;
    }

    public void setDailylist(List<Daily> dailylist) {
        this.dailylist = dailylist;
    }

    private List<Daily> dailylist = new ArrayList<Daily>();

    private int dailyNum;

    public int getDailyNum() {
        return dailyNum;
    }

    public void setDailyNum(int dailyNum) {
        this.dailyNum = dailyNum;
    }
}
