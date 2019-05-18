package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cn.edu.cczu.iot161g2.ccdict.R;

public class AppBarSearchFragment extends Fragment {
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
        return inflater.inflate(R.layout.app_bar_search, container, false);
    }
}
