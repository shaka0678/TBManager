package com.example.tbmanager;

import java.util.Calendar;

public class fenceD {
    String fullname,duration,residence;

    public fenceD() {}
    public fenceD(String fullname, String duration, String residence){
        this.fullname = fullname;
        this.duration = duration;
        this.residence = residence;
    }
    public String getFullname() {
        return fullname;
    }
   public String getDuration() {
        return duration;
   }
   public String getResidence() {
        return residence;
   }

    public Calendar getCreationDate() {
        return null;
    }
}
