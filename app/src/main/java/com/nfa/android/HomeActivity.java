package com.nfa.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.nfa.android.listeners.ConnectionListener;
import com.nfa.android.models.Course;
import com.nfa.android.models.CourseType;
import com.nfa.android.models.Student;
import com.nfa.android.utils.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, ConnectionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String api = "https://nfattend.firebaseapp.com/api/students/";

    private DrawerLayout drawer;

    private List<Course> courses;
    private ConnectionManager manager;
    private Student student;
    private HandlerThread thread;

    private HomeActive homeActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeActive = new HomeActive();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeActive).commit();
            navigationView.setCheckedItem(R.id.nav_Home);
        }

        courses = new ArrayList<>();
        manager = new ConnectionManager(api, this);
        manager.retrieveStudent(this.getIntent().getStringExtra("STUDENT_ID"));
        manager.retrieveCourses(this.getIntent().getStringExtra("STUDENT_ID"));

        thread = new HandlerThread("BACKGROUND");
        thread.start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_Home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeActive).commit();
                break;
            case R.id.nav_statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatisticsActivity()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatActivity()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsActivity()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initializeStudent(JSONObject object) {
        try {
            String id = object.getString("id");
            String first_name = object.getString("first_name");
            String last_name = object.getString("last_name");
            String email = object.getString("email");
            String device = object.getString("device");
            student = new Student(first_name, last_name, email, id, device);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeActive.updateUI();
                }
            });
        } catch (JSONException ex) {
            Log.d(TAG, "could not create student: " + ex.getMessage());
        }
    }

    private void refreshCourseView(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                Log.d(TAG, "vsl: " + array.get(i));
                JSONObject o = array.getJSONObject(i);
                String id = o.getString("id");
                String name = o.getString("name");
                String section = o.getString("section");
                String number = o.getString("number");
                String professor = o.getString("professor");
                String jsonType = o.getString("type");
                JSONArray jsonTimes = o.getJSONArray("times");
                JSONArray jsonDates = o.getJSONArray("dates");
                String location = o.getString("location");
                List<String> times = new ArrayList<>();
                List<String> dates = new ArrayList<>();
                for (int x = 0; x < jsonTimes.length(); x++) {
                    times.add(jsonTimes.getString(x));
                }
                for (int x = 0; x < jsonDates.length(); x++) {
                    dates.add(jsonDates.getString(x));
                }
                CourseType type = CourseType.valueOf(jsonType.toUpperCase());
                courses.add(new Course(id, name, number, section, type, dates,
                        times.get(0), times.get(1), location, professor));
            }
            student.setCourses(courses);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeActive.updateAdapter();
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    HandlerThread getThread() {
        return thread;
    }

    List<Course> getCourses() {
        return courses;
    }

    Student getStudent() {
        return student;
    }

    @Override
    public void onConnectionFinish(String eventType, JSONObject object) {
        switch (eventType) {
            case "Student":
                initializeStudent(object);
                break;
            case "Courses":
                // refreshCourseView(object);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnectionFinish(String eventType, JSONArray array) {
        switch (eventType) {
            case "Student":
                // initializeStudent(array);
                break;
            case "Courses":
                refreshCourseView(array);
                break;
            default:
                break;
        }
    }
}
