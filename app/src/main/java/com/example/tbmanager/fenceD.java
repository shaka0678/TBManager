package com.example.tbmanager;

import java.util.Calendar;

public class fenceD {
    String paitentName,duration,residence;

    public fenceD() {}
    public fenceD(String patientName, String duration, String residence){
        this.paitentName = patientName;
        this.duration = duration;
        this.residence = residence;
    }
    public String getPatientName(){
        return paitentName;
    }
   public String getDuration() {
        return duration;
   }
   public String getResidence() {
        return residence;
   }


}
