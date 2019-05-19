package cn.edu.cczu.iot161g2.ccdict.activities;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.HistoryEntry;
import cn.edu.cczu.iot161g2.ccdict.events.SearchCompletedEvent;
import cn.edu.cczu.iot161g2.ccdict.events.SearchStateChangedEvent;
import cn.edu.cczu.iot161g2.ccdict.fragments.AppBarSearchFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.AppBarTitleFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.HomeFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.SearchHistoryFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.SearchResultFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.SettingsFragment;
import cn.edu.cczu.iot161g2.ccdict.fragments.TransFragment;
import im.r_c.android.dbox.DBox;
import im.r_c.android.dbox.DBoxCondition;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String COLUMN_NAME_KEYWORD = "keyword";

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

    private void replaceAppBarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_app_bar_container, fragment)
                .commit();
    }

    private void replaceMainFragment(Fragment fragment, String tag) {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() - 1; i++) {
            getSupportFragmentManager().popBackStack();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_container, fragment, tag)
                .commit();
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dict:
                replaceAppBarFragment(AppBarSearchFragment.newInstance());
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("home");
                if (fragment == null) {
                    fragment = HomeFragment.newInstance();
                }
                replaceMainFragment(fragment, "home");
                return true;
            case R.id.navigation_trans:
                replaceAppBarFragment(AppBarTitleFragment.newInstance(getString(R.string.title_trans)));
                replaceMainFragment(TransFragment.newInstance(), null);
                return true;
//            case R.id.navigation_workbook:
//                replaceAppBarFragment(AppBarTitleFragment.newInstance(getString(R.string.title_wordbook)));
//                return true;
            case R.id.navigation_settings:
                replaceAppBarFragment(AppBarTitleFragment.newInstance(getString(R.string.title_settings)));
                replaceMainFragment(SettingsFragment.newInstance(), null);
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
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchStateChanged(SearchStateChangedEvent event) {
        if (event.enabled) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_main_container, SearchHistoryFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchCompletedEvent(SearchCompletedEvent event) {
        Log.d(TAG, "onSearchCompletedEvent, keyword: " + event.keyword + ", results: " + event.results);

        // 将关键词添加到搜索历史
        Observable.just(event.keyword)
                .doOnNext(kw -> {
                    try {
                        List<HistoryEntry> redundant = DBox.of(HistoryEntry.class)
                                .find(new DBoxCondition().equalTo(COLUMN_NAME_KEYWORD, kw))
                                .results().all();
                        for (HistoryEntry entry : redundant) {
                            DBox.of(HistoryEntry.class).remove(entry);
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                    DBox.of(HistoryEntry.class).save(new HistoryEntry(kw));
                })
                .subscribeOn(Schedulers.io())
                .subscribe();

        event.results.sort((a, b) -> a.getWord().length() - b.getWord().length());

        getSupportFragmentManager()
                .popBackStack("result", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_container,
                        SearchResultFragment.newInstance(event.keyword, event.results.size() > 0 ? event.results.get(0) : null))
                .addToBackStack("result")
                .commit();
    }
}
