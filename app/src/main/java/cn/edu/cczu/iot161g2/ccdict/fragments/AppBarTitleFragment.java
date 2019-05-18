package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.edu.cczu.iot161g2.ccdict.R;

public class AppBarTitleFragment extends Fragment {
    private static final String PARAM_TITLE = "title";

    private String mTitle;

    public AppBarTitleFragment() {
    }

    public static AppBarTitleFragment newInstance(String title) {
        AppBarTitleFragment fragment = new AppBarTitleFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(PARAM_TITLE);
        } else {
            mTitle = getString(R.string.app_name);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.app_bar_title, container, false);
        TextView tv = v.findViewById(R.id.tv_title);
        tv.setText(mTitle);
        return v;
    }
}
