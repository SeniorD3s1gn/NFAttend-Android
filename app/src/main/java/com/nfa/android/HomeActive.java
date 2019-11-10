package com.nfa.android;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nfa.android.adapters.CourseAdapter;
import com.nfa.android.models.Student;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActive extends Fragment {

    private static final String TAG = HomeActive.class.getSimpleName();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("EEEE, MMMM d h:mm", Locale.ENGLISH);

    private DrawerLayout drawer;
    private CourseAdapter adapter;
    private RecyclerView courseView;

    private TextView welcomeView;
    private TextView homeDate;

    private HomeActivity homeActivity;

    private Student student;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        homeActivity = (HomeActivity) getActivity();

        courseView = view.findViewById(R.id.course_list);
        welcomeView = view.findViewById(R.id.home_welcome);
        homeDate = view.findViewById(R.id.home_date);

        adapter = new CourseAdapter(homeActivity, homeActivity.getCourses());
        courseView.setAdapter(adapter);

        courseView.setLayoutManager(new LinearLayoutManager(homeActivity));

        updateTime();
        return view;
    }

    void updateUI() {
        student = homeActivity.getStudent();
        welcomeView.setText(getString(R.string.home_welcome_message, student.getFirstName()));
        updateTime();
    }

    void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    private void updateTime() {
        final Handler handler = new Handler(homeActivity.getThread().getLooper());
        Runnable run = new Runnable() {
            @Override
            public void run() {
                homeDate.setText(getString(R.string.home_date_message, SDF.format(new Date())));
                handler.postDelayed(this, 1000 * 30);
            }
        };
        run.run();
    }
}



