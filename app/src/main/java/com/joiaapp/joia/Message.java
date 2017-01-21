package com.joiaapp.joia;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by arnell on 11/4/2016.
 */

public class Message {
    private int id;
    private String promptText;
    private String text;
    private int userId;
    private Date date;
    private Set<User> mentions = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFullText() {
        return promptText + " " + text;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSameDateAs(Message other) {
        return isSameDateAs(other.getDate());
    }

    public boolean isSameDateAs(Date other) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(other);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isSameYearAs(Date other) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(other);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public Set<User> getMentions() {
        return mentions;
    }

    public String toString() {
        return text + "\n-" + getMentionsStr();
    }

    public String getMentionsStr() {
        String mentionsStr = "";
        for (User m : mentions) {
            mentionsStr += m.getName() + ", ";
        }
        return mentionsStr;
    }
}
