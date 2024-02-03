//package com.example.todoapplication;
//
//import android.icu.text.SimpleDateFormat;
//
//import java.text.ParseException;
//import java.util.Date;
//import java.util.Locale;
//
//public class TestModel {
//    private String fullDate;
//
//    String date, taskdesc, taskstatus, tasktitle, time;
//
//    public Model() {
//    }
//
//
//    public Model(String fullDate,String taskdesc,String tasktitle, String time) {
//        this.fullDate = fullDate;
//        this.taskdesc = taskdesc;
//        this.taskstatus = taskstatus;
//        this.tasktitle = tasktitle;
//        this.time = time;
//    }
//
//
//    public String getFullDate() {
//        return fullDate;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getTaskdesc() {
//        return taskdesc;
//    }
//
//    public void setTaskdesc(String taskdesc) {
//        this.taskdesc = taskdesc;
//    }
//
//    public String getTaskstatus() {
//        return taskstatus;
//    }
//
//    public void setTaskstatus(String taskstatus) {
//        this.taskstatus = taskstatus;
//    }
//
//    public String getTasktitle() {
//        return tasktitle;
//    }
//
//    public void setTasktitle(String tasktitle) {
//        this.tasktitle = tasktitle;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
//
//    public String getDay() {
//        if (fullDate != null) {
//            try {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                Date date = dateFormat.parse(fullDate);
//                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
//                String fullDay = dayFormat.format(date);
//
//                // Return the first three letters of the day
//                return fullDay.substring(0, 3);
//            } catch (java.text.ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
//    }
//
//
//    public String getDate() {
//        if (fullDate != null) {
//            try {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                Date date = dateFormat.parse(fullDate);
//                SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("dd", Locale.getDefault());
//                return dayOfMonthFormat.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
//    }
//
//    public String getMonth() {
//        if (fullDate != null) {
//            try {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                Date date = dateFormat.parse(fullDate);
//                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
//                return monthFormat.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
//    }
//}
