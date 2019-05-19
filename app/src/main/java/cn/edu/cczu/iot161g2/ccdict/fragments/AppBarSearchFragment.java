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

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.events.SearchStateChangedEvent;
import cn.edu.cczu.iot161g2.ccdict.events.SearchConfirmedEvent;

public class AppBarSearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    private static final String TAG = "AppBarSearchFragment";

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
        MaterialSearchBar searchBar = view.findViewById(R.id.msb_search_bar);
        searchBar.setOnSearchActionListener(this);
        searchBar.setSuggestionsEnabled(false);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        Log.d(TAG, "onSearchStateChanged: " + enabled);
        EventBus.getDefault().post(new SearchStateChangedEvent(enabled));
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Log.d(TAG, "onSearchConfirmed: " + text);
        EventBus.getDefault().post(new SearchConfirmedEvent(text.toString()));
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
