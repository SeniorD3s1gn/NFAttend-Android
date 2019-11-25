package com.nfa.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private static int TIME = 15;

    private DrawerLayout drawer;
    private CourseAdapter adapter;
    private RecyclerView courseView;

    private TextView welcomeView;
    private TextView homeDate;
    private Button attendBtn;

    private HomeActivity homeActivity;

    private Student student;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        homeActivity = (HomeActivity) getActivity();

        courseView = view.findViewById(R.id.course_list);
        welcomeView = view.findViewById(R.id.home_welcome);
        homeDate = view.findViewById(R.id.home_date);
        attendBtn = view.findViewById(R.id.home_attend);

        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.startSearching()) {
                    displaySearching();
                }
            }
        });

        adapter = new CourseAdapter(homeActivity, homeActivity.getCourses());
        courseView.setAdapter(adapter);

        courseView.setLayoutManager(new LinearLayoutManager(homeActivity));

        updateTime();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void displaySearching() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("NF Attend");
        builder.setMessage("Tap the NFC tag to back of your device");
        builder.setCancelable(false);
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                homeActivity.stopSearching();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.show();
        updateSearchDisplay(dialog);
    }

    private void updateSearchDisplay(final AlertDialog dialog) {
        TIME = 15;
        final Handler handler = new Handler(homeActivity.getThread().getLooper());
        Runnable run = new Runnable() {
            @Override
            public void run() {
                final boolean searching = homeActivity.isSearching();
                homeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!searching || TIME < 1) {
                            dialog.dismiss();
                            homeActivity.stopSearching();
                        }
                    }
                });
                TIME--;
                handler.postDelayed(this, 1000);
            }
        };
        run.run();
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
                homeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homeDate.setText(getString(R.string.home_date_message, SDF.format(new Date())));
                    }
                });
                handler.postDelayed(this, 1000 * 30);
            }
        };
        run.run();
    }
}



