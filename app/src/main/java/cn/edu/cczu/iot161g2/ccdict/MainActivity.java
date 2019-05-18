package cn.edu.cczu.iot161g2.ccdict;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.edu.cczu.iot161g2.ccdict.events.SearchBarStateChangeEvent;
import cn.edu.cczu.iot161g2.ccdict.fragments.AppBarSearchFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.AppBarTitleFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.HomeFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.SearchHistoryFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        BottomNavigationView navView = findViewById(R.id.bnv_nav_view);
        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navView.setSelectedItemId(R.id.navigation_dict);
    }

    private void switchAppBarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_app_bar_container, fragment)
                .commit();
    }

    private void switchMainFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_container, fragment)
                .commit();
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dict:
                switchAppBarFragment(AppBarSearchFragment.newInstance());
                switchMainFragment(HomeFragment.newInstance());
                return true;
            case R.id.navigation_trans:
                switchAppBarFragment(AppBarTitleFragment.newInstance(getString(R.string.title_trans)));
                return true;
            case R.id.navigation_workbook:
                switchAppBarFragment(AppBarTitleFragment.newInstance(getString(R.string.title_wordbook)));
                return true;
            case R.id.navigation_settings:
                switchAppBarFragment(AppBarTitleFragment.newInstance(getString(R.string.title_settings)));
                switchMainFragment(SettingsFragment.newInstance());
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchBarStateChange(SearchBarStateChangeEvent event) {
        if (event.enabled) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_main_container, SearchHistoryFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .popBackStack();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//
//        }
//    }
}
