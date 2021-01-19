package com.example.notesapp;

import android.util.Log;

public class Note {
    private long id;
    private int locked;
    private String title;
    private String content;
    private String date;
    private String time;

    Note(){}
    Note(String title, String content, String date, String time, int locked){
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time ;
        this.locked = locked;
        this.id = -1;
    }
    Note(long id, String title, String content, String date, String time, int locked){
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time ;
        this.locked = locked;
    }

    public long getId() {
        return id;
    }

    public int getLocked() { return locked; }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() { return content; }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void logNote(){
        if (id == -1)
            Log.d("debug","noId : " + title + ", " + time + ", " + date + ", isLocked " + locked);
        else
            Log.d("debug",id + " : " + title + ", " + time + ", " + date + ", isLocked " + locked);
    }

    public int compareTime(Note a){
        int result = 0;
        long thisDate, aDate;
        thisDate = Long.valueOf(this.getDate().substring(6,9))*366 + Long.valueOf(this.getDate().substring(3,4))*32 +
                Long.valueOf(this.getDate().substring(0,1));
        aDate = Long.valueOf(a.getDate().substring(6,9))*366 + Long.valueOf(a.getDate().substring(3,4))*32 +
                Long.valueOf(a.getDate().substring(0,1));
        if (thisDate > aDate)
            return 1;
        else if (thisDate < aDate )
            return -1;
        else
            if (this.getTime().endsWith("PM") && a.getTime().endsWith("AM"))
                return 1;
            else if (this.getTime().endsWith("AM") && a.getTime().endsWith("PM"))
                return -1;
            else{
                thisDate = Long.valueOf(this.getTime().toCharArray()[0] + this.getTime().toCharArray()[1])*60 +
                        Long.valueOf(this.getTime().toCharArray()[3] + this.getTime().toCharArray()[4]);
                aDate = Long.valueOf(a.getTime().toCharArray()[0] + a.getTime().toCharArray()[1])*60 +
                        Long.valueOf(a.getTime().toCharArray()[3] + a.getTime().toCharArray()[4]);
                return Long.compare(thisDate, aDate);
            }
    }
}
