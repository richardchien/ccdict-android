package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.HistoryEntry;
import cn.edu.cczu.iot161g2.ccdict.events.SearchConfirmedEvent;
import im.r_c.android.commonadapter.CommonAdapter;
import im.r_c.android.commonadapter.ViewHolder;
import im.r_c.android.dbox.DBox;

public class SearchHistoryFragment extends Fragment {
    private ListView mHistoryListView;
    private BaseAdapter mHistoryListViewAdapter;
    private List<HistoryEntry> mHistoryEntryList = new LinkedList<>();
    private Lock mLock = new ReentrantLock();

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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        mHistoryListView = view.findViewById(R.id.lv_search_history);
        mHistoryListViewAdapter = new CommonAdapter<HistoryEntry>(getContext(), mHistoryEntryList, R.layout.search_history_list_item) {
            @Override
            public void onPostBindViewHolder(ViewHolder viewHolder, HistoryEntry historyEntry) {
                viewHolder.setViewText(R.id.tv_item_keyword, historyEntry.getKeyword());
                viewHolder.getView(R.id.btn_item_delete).setOnClickListener(v -> {
                    HistoryEntry entry = mHistoryEntryList.remove(viewHolder.getPosition());
                    notifyDataSetChanged();
                });
            }
        };
        mHistoryListView.setAdapter(mHistoryListViewAdapter);
        mHistoryListView.setOnItemClickListener((parent, view1, position, id) -> {
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSearchConfirmed(SearchConfirmedEvent event) {
        HistoryEntry entry = new HistoryEntry(event.keyword);
        DBox.of(HistoryEntry.class).save(entry);
        mLock.lock();
        mHistoryEntryList.add(0, entry);
        mLock.unlock();
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        EventBus.getDefault().post(new DataSetChangedEvent());
    }

    private static class DataSetChangedEvent {
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onDataSetChanged(DataSetChangedEvent event) {
        mHistoryListViewAdapter.notifyDataSetChanged();
    }
}
