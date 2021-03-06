package com.example.studyo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    //  fragmentSavedStates store the state instance of three main screens
    private Map<String, Fragment.SavedState> fragmentSavedStates = new HashMap<>();
    //  fragmentTags store the string tags of each three main screens
    private String[] fragmentTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentTags = getResources().getStringArray(R.array.fragment_tags);

        getSupportActionBar().hide();   //  hide the app title bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.action_timer);
        bottomNav.setOnNavigationItemSelectedListener(new navItemListener());

        //  Check if an old fragment is already there
        //  !! Android persists the Fragment layout and associated back stack when an Activity is restarted due to a configuration change !!
        //  https://stackoverflow.com/questions/8474104/android-fragment-lifecycle-over-orientation-changes
        if (fragmentManager.getFragments().size() == 0) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new TimerScreenFragment(), fragmentTags[1]).commit();
        }
    }

    private class navItemListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            String nextFragmentTag;

            //  Save the state of the current fragment
            Fragment currentFragment = fragmentManager.getFragments().get(0);
            Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(currentFragment);
            fragmentSavedStates.put(currentFragment.getTag(), savedState);

            switch (menuItem.getItemId()) {
                case R.id.action_planner:
                    selectedFragment = new PlannerScreenFragment();
                    nextFragmentTag = fragmentTags[0];
                    break;
                case R.id.action_stats:
                    selectedFragment = new StatisticScreenFragment();
                    nextFragmentTag = fragmentTags[2];
                    break;
                default:
                    selectedFragment = new TimerScreenFragment();
                    nextFragmentTag = fragmentTags[1];
            }

            //  If the fragment that we're navigating to has a saved state (e.g. the timer is running)
            //  Restore that states
            Log.i("States Array", fragmentSavedStates.toString());
            if (fragmentSavedStates.get(nextFragmentTag) != null) {
                if (!selectedFragment.isAdded())
                    selectedFragment.setInitialSavedState(fragmentSavedStates.get(nextFragmentTag));
            }

            fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment, nextFragmentTag).commit();

            return true;
        }
    }
}