package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mancj.materialsearchbar.MaterialSearchBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.events.SearchBarStateChangeEvent;

public class AppBarSearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    private static final String TAG = "AppBarSearchFragment";

    private MaterialSearchBar mSearchBar;

    public AppBarSearchFragment() {
    }

    public static AppBarSearchFragment newInstance() {
        AppBarSearchFragment fragment = new AppBarSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.app_bar_search, container, false);
        initView(v);
        return v;
    }

    private void initView(View view) {
        mSearchBar = view.findViewById(R.id.msb_search_bar);
        mSearchBar.setOnSearchActionListener(this);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        Log.d(TAG, "onSearchStateChanged: " + enabled);
        EventBus.getDefault().post(new SearchBarStateChangeEvent(enabled));
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Log.d(TAG, "onSearchConfirmed: " + text);
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
