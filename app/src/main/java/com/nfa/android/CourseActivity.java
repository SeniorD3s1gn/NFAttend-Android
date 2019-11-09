package com.nfa.android;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CourseActivity extends AppCompatActivity {

    private TextView CourseInfo;
    private TextView CourseType;
    private TextView CourseTime;
    private TextView CourseLocation;
    private TextView CoursePro;
    private CalendarView CourseCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        CourseInfo = findViewById(R.id.CourseInfo);
        CourseType = findViewById(R.id.CourseType);
        CourseTime = findViewById(R.id.CourseTime);
        CourseLocation = findViewById(R.id.CourseLocation);
        CoursePro = findViewById(R.id.CoursePro);
        CourseCal = findViewById(R.id.CourseCal);

    }
}