package com.example.todoapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.List;

public class CustomDatePickerDialog extends DatePickerDialog {

    private List<String> taskDates;

    public CustomDatePickerDialog(Context context, OnDateSetListener listener, int year, int month, int dayOfMonth, List<String> taskDates) {
        super(context, listener, year, month, dayOfMonth);
        this.taskDates = taskDates;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the "Cancel" and "OK" buttons
        Resources resources = getContext().getResources();
        int buttonPanelId = resources.getIdentifier("android:id/buttonPanel", null, null);

        if (buttonPanelId > 0) {
            findViewById(buttonPanelId).setVisibility(android.view.View.GONE);
        }

    }

}
