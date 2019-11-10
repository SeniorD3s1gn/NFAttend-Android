package com.nfa.android;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nfa.android.listeners.ConnectionListener;
import com.nfa.android.models.Course;
import com.nfa.android.utils.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseActivity extends AppCompatActivity implements ConnectionListener {

    private static final String TAG = Course.class.getSimpleName();
    private static final String api = "https://nfattend.firebaseapp.com/api/faculty/";

    private TextView CourseInfo;
    private TextView CourseType;
    private TextView CourseTime;
    private TextView CourseLocation;
    private TextView CourseDate;
    private TextView CourseProf;
    private CalendarView CourseCal;

    private String professorName;

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

        ConnectionManager manager = new ConnectionManager(api, this);
        manager.retrieveProfessor(course.getProf());
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
        //fill calander method
    }

    private void updateCourseInformation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CourseProf.setText(getString(R.string.course_item_prof, professorName));
            }
        });
    }

    @Override
    public void onConnectionFinish(String eventType, JSONObject object) {
        if (eventType.equals("Professor")) {
            try {
                professorName = object.getString("first_name") + " " + object.getString("last_name");
                updateCourseInformation();
            } catch (JSONException ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void onConnectionFinish(String eventType, JSONArray array) {

    }
}