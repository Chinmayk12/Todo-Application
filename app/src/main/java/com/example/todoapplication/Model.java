package com.example.todoapplication;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ParseException;

import java.util.Date;
import java.util.Locale;

public class Model {

    String date, taskdesc, taskstatus, tasktitle, time;
    String fullDate;

    public Model() {
    }

    public Model(String date, String taskdesc, String taskstatus, String tasktitle, String time) {
        this.date = date;
        this.taskdesc = taskdesc;
        this.taskstatus = taskstatus;
        this.tasktitle = tasktitle;
        this.time = time;

    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }


    public void setDate(String date) {
        this.date = date;
        setFullDate(date);
    }

    public String getTaskdesc() {
        return taskdesc;
    }

    public void setTaskdesc(String taskdesc) {
        this.taskdesc = taskdesc;
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }

    public String getTasktitle() {
        return tasktitle;
    }

    public void setTasktitle(String tasktitle) {
        this.tasktitle = tasktitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Add a method to extract day, date, and month
    public String getDay() {
        // Assuming the date is stored in the format "dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = dateFormat.parse(fullDate);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            return dayFormat.format(date);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getDate() {
        // Assuming the date is stored in the format "dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = dateFormat.parse(fullDate);
            SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("dd", Locale.getDefault());
            return dayOfMonthFormat.format(date);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getMonth() {
        // Assuming the date is stored in the format "dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = dateFormat.parse(fullDate);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            return monthFormat.format(date);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

}
