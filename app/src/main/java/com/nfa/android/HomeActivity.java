package com.nfa.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.nfa.android.fragments.NFCReadFragment;
import com.nfa.android.listeners.ConnectionListener;
import com.nfa.android.listeners.NFCListener;
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
        NavigationView.OnNavigationItemSelectedListener, ConnectionListener, NFCListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String api = "https://nfattend.firebaseapp.com/api/students/";

    private DrawerLayout drawer;

    private List<Course> courses;
    private Student student;
    private HandlerThread thread;

    private HomeActive homeActive;
    private NFCReadFragment readFragment;

    private NfcAdapter adapter;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String comboValue = "alertValue";
    public boolean checked =false;
    public static final String notifValue = "notifValue";
    private boolean isDialogDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkLocationPermission();

        homeActive = new HomeActive();
        adapter = NfcAdapter.getDefaultAdapter(this);

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
        ConnectionManager manager = new ConnectionManager(api, this);
        manager.retrieveStudent(this.getIntent().getStringExtra("STUDENT_ID"));
        manager.retrieveCourses(this.getIntent().getStringExtra("STUDENT_ID"));

        thread = new HandlerThread("BACKGROUND");
        thread.start();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{ techDetected, tagDetected, ndefDetected };

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(adapter != null) {
            adapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: " + intent.getAction());

        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            MifareUltralight mifareUltralight = MifareUltralight.get(tag);

            if (isDialogDisplayed) {
                    readFragment = (NFCReadFragment) getSupportFragmentManager()
                            .findFragmentByTag(NFCReadFragment.TAG);
                    if (readFragment != null) {
                        readFragment.onNfcDetected(mifareUltralight);
                    }
            }
        }
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

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
    }

    public void showReadFragment() {
        readFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
        if (readFragment == null) {
            readFragment = NFCReadFragment.newInstance();
        }
        readFragment.show(getSupportFragmentManager(), NFCReadFragment.TAG);
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
    public void savePref(int position)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(comboValue, position);
        editor.apply();
    }


    public void saveNSwitch(boolean checked)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(notifValue, checked);
        editor.apply();
    }

    public boolean getNSwitch()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        boolean checked = sharedPreferences.getBoolean(notifValue, false);
        return  checked;
    }

    public int getAlertValue()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        int position = sharedPreferences.getInt(comboValue, 0);
        return position;
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

    public void checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode)
        {
            case 1:
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("TAG", "Location Permission granted");
                }
                else
                {
                    Log.d("TAG", "Location Permission failed");
                }
            }
        }
    }
}
