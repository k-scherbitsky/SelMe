package com.selme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.selme.R;
import com.selme.activity.auth.LoginActivity;
import com.selme.entity.UserEntity;
import com.selme.fragments.CreatePostFragment;
import com.selme.fragments.DashboardFragment;
import com.selme.fragments.ProfileFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTextMessage;

    private DashboardFragment dashboardFragment;
    private CreatePostFragment createPostFragment;
    private ProfileFragment profileFragment;

    private BottomNavigationView
            .OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_add_post:
                fragment = createPostFragment;
                break;
            case R.id.navigation_dashboard:
                fragment = dashboardFragment;
                break;
            case R.id.navigation_profile:
                fragment = profileFragment;
                break;
        }

        return loadFragment(fragment);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);

        profileFragment = new ProfileFragment();
        dashboardFragment = new DashboardFragment();
        createPostFragment = new CreatePostFragment();

        loadFragment(dashboardFragment);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            Log.d(TAG, "loadFragment: success");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        } else {
            Log.d(TAG, "loadFragment: failure");
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionSignOut:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        Log.d(TAG, "signOut");
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
