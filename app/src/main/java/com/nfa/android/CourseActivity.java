package com.nfa.android;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nfa.android.models.Course;

public class CourseActivity extends AppCompatActivity {

    private TextView CourseInfo;
    private TextView CourseType;
    private TextView CourseTime;
    private TextView CourseLocation;
    private TextView CourseDate;
    private TextView CourseProf;
    private CalendarView CourseCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        CourseInfo = findViewById(R.id.CourseInfo);
        CourseType = findViewById(R.id.CourseType);
        CourseDate = findViewById(R.id.CourseDate);
        CourseTime = findViewById(R.id.CourseTime);
        CourseLocation = findViewById(R.id.CourseLocation);
        CourseProf = findViewById(R.id.CoursePro);
        CourseCal = findViewById(R.id.CourseCal);

        Course course = this.getIntent().getParcelableExtra("Course");
        fill(course);
    }

    public void fill(Course course)
    {
        CourseInfo.setText(getString(R.string.course_item_name, course.getName(), course.getNumber()
                , course.getSection()));
        CourseType.setText(getString(R.string.course_item_type, course.getType()));
        CourseDate.setText(getString(R.string.course_item_date, course.translateDays()));
        CourseTime.setText(getString(R.string.course_item_time, course.getStartTime(),
                course.getEndTime()));
        CourseLocation.setText(getString(R.string.course_item_location, course.getLocation()));
        CourseProf.setText(getString(R.string.course_item_prof, course.getProf()));
        //fill calander method
    }
}