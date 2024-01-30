package com.example.todoapplication;

public class Model {

    long date;
    String day,month,status,taskdesc,tasktitle,time;

    public Model() {
    }

    public Model(long date, String day, String month, String status, String taskdesc, String tasktitle, String time) {
        this.date = date;
        this.day = day;
        this.month = month;
        this.status = status;
        this.taskdesc = taskdesc;
        this.tasktitle = tasktitle;
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate( long date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskdesc() {
        return taskdesc;
    }

    public void setTaskdesc(String taskdesc) {
        this.taskdesc = taskdesc;
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



}
