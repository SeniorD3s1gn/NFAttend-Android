package com.nfa.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

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

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeActive()).commit();
            navigationView.setCheckedItem(R.id.nav_Home);
        }
    }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId())
            {
                case R.id.nav_Home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeActive()).commit();
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
}
