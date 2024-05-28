package com.example.weatherwise.model;

import com.google.type.DateTime;

public class Notification {

    private String title;
    private String extraMessage;
    private int icon;
    private DateTime dateTimeCreated;

    public Notification(String title, String extraMessage, int icon, DateTime dateTimeCreated) {
        this.title = title;
        this.extraMessage = extraMessage;
        this.icon = icon;
        this.dateTimeCreated = dateTimeCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtraMessage() {
        return extraMessage;
    }

    public void setExtraMessage(String extraMessage) {
        this.extraMessage = extraMessage;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public DateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(DateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }
}
