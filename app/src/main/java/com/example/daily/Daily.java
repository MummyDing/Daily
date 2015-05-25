package com.example.daily;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by MummyDing on 2015/5/20.
 */
public class Daily extends DataSupport{
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private int id;
    private String title;
    private String content;
    private Date date;
    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
