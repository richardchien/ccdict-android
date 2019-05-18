package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.HistoryEntry;
import im.r_c.android.commonadapter.CommonAdapter;
import im.r_c.android.commonadapter.ViewHolder;

public class SearchHistoryFragment extends Fragment {
    private ListView mSearchHistoryListView;
    private List<HistoryEntry> mHistoryEntryList = new ArrayList<>();

    {
        mHistoryEntryList.add(new HistoryEntry("测试"));
        mHistoryEntryList.add(new HistoryEntry("test"));
        mHistoryEntryList.add(new HistoryEntry("hello"));
        mHistoryEntryList.add(new HistoryEntry("测试"));
        mHistoryEntryList.add(new HistoryEntry("test"));
        mHistoryEntryList.add(new HistoryEntry("hello"));
        mHistoryEntryList.add(new HistoryEntry("测试"));
        mHistoryEntryList.add(new HistoryEntry("test"));
        mHistoryEntryList.add(new HistoryEntry("hello"));
        mHistoryEntryList.add(new HistoryEntry("测试"));
        mHistoryEntryList.add(new HistoryEntry("test"));
        mHistoryEntryList.add(new HistoryEntry("hello"));
        mHistoryEntryList.add(new HistoryEntry("测试"));
        mHistoryEntryList.add(new HistoryEntry("test"));
        mHistoryEntryList.add(new HistoryEntry("hello"));
        mHistoryEntryList.add(new HistoryEntry("测试"));
        mHistoryEntryList.add(new HistoryEntry("test"));
        mHistoryEntryList.add(new HistoryEntry("hello"));
    }

    public SearchHistoryFragment() {
    }

    public static SearchHistoryFragment newInstance() {
        return new SearchHistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_history, container, false);
        initView(v);
        return v;
    }

    private void initView(View view) {
        mSearchHistoryListView = view.findViewById(R.id.lv_search_history);
        mSearchHistoryListView.setAdapter(new CommonAdapter<HistoryEntry>(getContext(), mHistoryEntryList, R.layout.search_history_list_item) {
            @Override
            public void onPostBindViewHolder(ViewHolder viewHolder, HistoryEntry historyEntry) {
                viewHolder.setViewText(R.id.tv_item_keyword, historyEntry.getKeyword());
            }
        });
        mSearchHistoryListView.setOnItemClickListener((parent, view1, position, id) -> {
            Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
        });
    }
}
