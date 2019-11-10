package com.nfa.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
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

public class HomeActive extends Fragment implements ConnectionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String api = "https://nfattend.firebaseapp.com/api/students/";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("EEEE, MMMM d h:mm", Locale.ENGLISH);

    private DrawerLayout drawer;
    private CourseAdapter adapter;
    private RecyclerView courseView;

    private TextView welcomeView;
    private TextView homeDate;

    private List<Course> courses;
    private ConnectionManager manager;
    private Student student;
    private HandlerThread thread;
    private HomeActivity homeactivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        homeactivity = (HomeActivity) getActivity();
        courses = new ArrayList<>();
        manager = new ConnectionManager(api, this);
        manager.retrieveStudent(homeactivity.getIntent().getStringExtra("STUDENT_ID"));
        manager.retrieveCourses(homeactivity.getIntent().getStringExtra("STUDENT_ID"));

        courseView = view.findViewById(R.id.course_list);
        welcomeView = view.findViewById(R.id.home_welcome);
        homeDate = view.findViewById(R.id.home_date);

        adapter = new CourseAdapter(homeactivity, courses);
        courseView.setAdapter(adapter);

        courseView.setLayoutManager(new LinearLayoutManager(homeactivity));
        // loadFakeCourses();

        thread = new HandlerThread("BACKGROUND");
        thread.start();

        updateTime();
        return view;
    }

    private void updateUI() {
        homeactivity.runOnUiThread(new Runnable() {
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

    private void initializeStudent(JSONObject object) {
        try {
            String id = object.getString("id");
            String first_name = object.getString("first_name");
            String last_name = object.getString("last_name");
            String email = object.getString("email");
            String device = object.getString("device");
            student = new Student(first_name, last_name, email, id, device);
            updateUI();
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
            homeactivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
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



