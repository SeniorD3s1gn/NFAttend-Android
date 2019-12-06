package com.nfa.android;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nfa.android.adapters.CourseAdapter;
import com.nfa.android.models.Student;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActive extends Fragment {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("EEEE, MMMM d h:mm", Locale.ENGLISH);

    // private DrawerLayout drawer;
    private CourseAdapter adapter;
    private boolean attached;
    private TextView welcomeView;
    private TextView homeDate;

    private HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        homeActivity = (HomeActivity) getActivity();
        attached =true;
        RecyclerView courseView = view.findViewById(R.id.course_list);
        welcomeView = view.findViewById(R.id.home_welcome);
        homeDate = view.findViewById(R.id.home_date);
        Button attendBtn = view.findViewById(R.id.home_attend);

        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.showReadFragment();
            }
        });

        adapter = new CourseAdapter(homeActivity, homeActivity.getCourses());
        courseView.setAdapter(adapter);

        courseView.setLayoutManager(new LinearLayoutManager(homeActivity));

        updateTime();
        return view;
    }

    void updateUI() {
        Student student = homeActivity.getStudent();
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
                homeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(attached) {
                            homeDate.setText(getString(R.string.home_date_message, SDF.format(new Date())));
                        }
                    }
                });
                handler.postDelayed(this, 1000 * 30);
            }
        };
        run.run();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attached =false;

    }
}



