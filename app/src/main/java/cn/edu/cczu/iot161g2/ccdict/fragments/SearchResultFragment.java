package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;
import cn.edu.cczu.iot161g2.ccdict.databinding.FragmentSearchResultBinding;

public class SearchResultFragment extends Fragment {
    private static final String PARAM_KEYWORD = "keyword";
    private static final String PARAM_DICT_ENTRY = "dict entry";

    private String mKeyword;
    private DictEntry mEntry;

    public SearchResultFragment() {
    }

    public static SearchResultFragment newInstance(String keyword, DictEntry entry) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_KEYWORD, keyword);
        args.putSerializable(PARAM_DICT_ENTRY, entry);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mKeyword = bundle.getString(PARAM_KEYWORD);
            mEntry = (DictEntry) bundle.getSerializable(PARAM_DICT_ENTRY);
        }

        if (mEntry == null) {
            mEntry = new DictEntry(mKeyword != null ? mKeyword : "", "未找到相关释义");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSearchResultBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_result, container, false);
        binding.setEntry(mEntry);
        return binding.getRoot();
    }
}
