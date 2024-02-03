package com.example.todoapplication;

public class Model {

    String day,date,month,taskdesc,taskstatus,tasktitle,time;

    public Model() {
    }
    public Model(String date,String day,String month, String taskdesc, String taskstatus, String tasktitle, String time) {
        this.day = day;
        this.date = date;
        this.month = month;
        this.taskdesc = taskdesc;
        this.taskstatus = taskstatus;
        this.tasktitle = tasktitle;
        this.time = time;
    }

    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
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

    public void setTasktitle( String tasktitle) {
        this.tasktitle = tasktitle;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}