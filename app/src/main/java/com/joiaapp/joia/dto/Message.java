package com.joiaapp.joia.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by arnell on 11/4/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class Message {
    private Integer id;
    private String prompt;
    private Integer prompt_id;
    private transient Prompt promptObj;
    private String text;
    private int group_id;
    private int user_id;
    private User user;
    private Date created_at;
    private Date updated_at;
    private List<Mention> mentions = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public Integer getPromptId() {
        return prompt_id;
    }

    public Prompt getPromptObj() {
        return promptObj;
    }

    private void setPromptObj(Prompt promptObj) {
        this.promptObj = promptObj;
        this.prompt = promptObj.getPhrase();
        this.prompt_id = promptObj.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(Prompt prompt, String userText) {
        setPromptObj(prompt);
        this.text = userText;
    }

    public String getTextWithPrompt() {
        return prompt + " " + text;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date date) {
        this.created_at = date;
    }

    public boolean isSameDateAs(Message other) {
        return isSameDateAs(other.getCreatedAt());
    }

    public boolean isSameDateAs(Date other) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(getCreatedAt());
        cal2.setTime(other);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isSameYearAs(Date other) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(created_at);
        cal2.setTime(other);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }

    public String toString() {
        return getTextWithPrompt() + "\n-" + getMentionsStr();
    }

    public String getMentionsStr() {
        if (mentions.isEmpty()) {
            return "";
        }
        String mentionsStr = "With: ";
        for (Mention m : mentions) {
            mentionsStr += m.getUser().getName() + ", ";
        }
        return mentionsStr.substring(0, mentionsStr.length()-2);
    }
}
