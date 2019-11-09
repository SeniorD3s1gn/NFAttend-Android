package com.nfa.android;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;

import com.nfa.android.adapters.CourseAdapter;
import com.nfa.android.listeners.ConnectionListener;
import com.nfa.android.models.Course;
import com.nfa.android.models.CourseType;
import com.nfa.android.models.Student;
import com.nfa.android.utils.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements ConnectionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String api = "https://nfattend.firebaseapp.com/api/students/";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("EEEE, MMMM d hh:mm", Locale.ENGLISH);

    private DrawerLayout drawer;
    private CourseAdapter adapter;
    private RecyclerView courseView;

    private TextView welcomeView;
    private TextView homeDate;

    private List<Course> courses;
    private ConnectionManager manager;
    private Student student;

    private HandlerThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        courses = new ArrayList<>();
        manager = new ConnectionManager(api, this);
        manager.retrieveStudent(this.getIntent().getStringExtra("STUDENT_ID"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        courseView = findViewById(R.id.course_list);
        welcomeView = findViewById(R.id.home_welcome);
        homeDate = findViewById(R.id.home_date);

        adapter = new CourseAdapter(this, courses);
        courseView.setAdapter(adapter);

        courseView.setLayoutManager(new LinearLayoutManager(this));
        loadFakeCourses();

        thread = new HandlerThread("BACKGROUND");
        thread.start();

        updateTime();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadFakeCourses() {
        Course c1 = new Course("0000", "CSCI", "300", "M01",
                CourseType.LECTURE, Arrays.asList("Mon", "Tues"), "9:00am",
                "12:35pm", "26 West 61st Street 203", "Dr.Guy" );
        courses.add(c1);
        Course c2 = new Course("0001", "FCWR", "301", "M01",
                CourseType.LECTURE, Arrays.asList("Mon", "Tues"), "2:20pm",
                "3:45pm", "Edward Guiliano Global Center 415", "Dr.Ben" );
        courses.add(c2);
        adapter.notifyDataSetChanged();
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                welcomeView.setText(getString(R.string.home_welcome_message, student.getFirstName()));
                updateTime();
            }
        });
    }

    private void updateTime() {
        final Handler handler = new Handler(thread.getLooper());
        Runnable run = new Runnable() {
            @Override
            public void run() {
                homeDate.setText(getString(R.string.home_date_message, SDF.format(new Date())));
                handler.postDelayed(this, 1000 * 30);
            }
        };
        run.run();
    }

    @Override
    public void onConnectionFinish(String eventType, JSONObject object) {
        if (eventType.equals("Student")) {
            try {
                String id = object.getString("id");
                String first_name = object.getString("first_name");
                String last_name = object.getString("last_name");
                String email = object.getString("email");
                String device = object.getString("device");
                if (object.has("courses")) {
                    JSONArray courses = object.getJSONArray("courses");
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject o = courses.getJSONObject(i);
                        Log.d(TAG, o.toString());
                    }
                }
                student = new Student(first_name, last_name, email, id, device);
                updateUI();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
