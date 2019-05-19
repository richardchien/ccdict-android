package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.HistoryEntry;
import cn.edu.cczu.iot161g2.ccdict.events.SearchEvent;
import im.r_c.android.commonadapter.CommonAdapter;
import im.r_c.android.commonadapter.ViewHolder;
import im.r_c.android.dbox.DBox;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchHistoryFragment extends Fragment {
    private BaseAdapter mHistoryListViewAdapter;
    private List<HistoryEntry> mHistoryEntryList = new LinkedList<>();

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadSearchHistory();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initView(View view) {
        ListView historyListView = view.findViewById(R.id.lv_search_history);
        mHistoryListViewAdapter = new CommonAdapter<HistoryEntry>(getContext(), mHistoryEntryList, R.layout.search_history_list_item) {
            @Override
            public void onPostBindViewHolder(ViewHolder viewHolder, HistoryEntry historyEntry) {
                viewHolder.setViewText(R.id.tv_item_keyword, historyEntry.getKeyword());
                viewHolder.getView(R.id.btn_item_delete).setOnClickListener(v -> {
                    HistoryEntry entry = mHistoryEntryList.remove(viewHolder.getPosition());
                    EventBus.getDefault().post(new RemoveEntryEvent(entry));
                    notifyDataSetChanged();
                });
            }
        };
        historyListView.setAdapter(mHistoryListViewAdapter);
        historyListView.setOnItemClickListener((parent, view1, position, id) -> {
            EventBus.getDefault().post(new SearchEvent(mHistoryEntryList.get(position).getKeyword()));
        });
    }

    private void loadSearchHistory() {
        Observable.just("")
                .map(s -> DBox.of(HistoryEntry.class).findAll().orderByDesc("id").results().all())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        historyEntries -> {
                            mHistoryEntryList.clear();
                            mHistoryEntryList.addAll(historyEntries);
                            notifyDataSetChanged();
                        },
                        throwable -> {
                        }
                );
    }

    /**
     * 保证在 UI 线程更新搜索历史列表.
     */
    private void notifyDataSetChanged() {
        EventBus.getDefault().post(new DataSetChangedEvent());
    }

    private static class DataSetChangedEvent {
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onDataSetChanged(DataSetChangedEvent event) {
        mHistoryListViewAdapter.notifyDataSetChanged();
    }

    private static class RemoveEntryEvent {
        final HistoryEntry entry;

        private RemoveEntryEvent(HistoryEntry entry) {
            this.entry = entry;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onRemoveEntry(RemoveEntryEvent event) {
        DBox.of(HistoryEntry.class).remove(event.entry);
    }
}
