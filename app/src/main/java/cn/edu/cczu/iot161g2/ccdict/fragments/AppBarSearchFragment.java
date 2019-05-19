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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;
import cn.edu.cczu.iot161g2.ccdict.events.SearchCompletedEvent;
import cn.edu.cczu.iot161g2.ccdict.events.SearchEvent;
import cn.edu.cczu.iot161g2.ccdict.events.SearchStateChangedEvent;
import im.r_c.android.dbox.DBox;
import im.r_c.android.dbox.DBoxCondition;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * 顶部搜索栏.
 */
public class AppBarSearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    private static final String TAG = "AppBarSearchFragment";

    private static final String COLUMN_NAME_WORD = "word";
    private static final String COLUMN_NAME_EXPLANATION = "explanation";

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
        mSearchBar.setSuggestionsEnabled(false); // 禁用搜索栏组件自带的搜索历史和搜索建议
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

    @Override
    public void onSearchStateChanged(boolean enabled) {
        Log.d(TAG, "onSearchStateChanged: " + enabled);
        EventBus.getDefault().post(new SearchStateChangedEvent(enabled));
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Log.d(TAG, "onSearchConfirmed: " + text);
        String keyword = text.toString().trim();
        if (keyword.length() > 0) {
            EventBus.getDefault().post(new SearchEvent(keyword));
        }
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }

    /**
     * 实现搜索逻辑.
     * 由于有多种途径触发搜索, 为降低耦合这里使用 EventBus 订阅搜索事件, 而不是直接提供方法调用.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearch(SearchEvent event) {
        mSearchBar.disableSearch(); // 恢复搜索栏到原始状态

        // 搜索关键词, 然后通知相关组件搜索已完成, 事件中包含搜索结果
        Observable.just(event.keyword)
                .map(kw -> DBox.of(DictEntry.class)
                        .find(new DBoxCondition()
                                .startsWith(COLUMN_NAME_WORD, kw)
                                .or()
                                .contains(COLUMN_NAME_EXPLANATION, kw))
                        .results()
                        .all())
                .subscribeOn(Schedulers.io())
                .subscribe(dictEntries -> EventBus.getDefault().post(new SearchCompletedEvent(event.keyword, dictEntries)),
                        throwable -> EventBus.getDefault().post(new SearchCompletedEvent(event.keyword, null)));
    }
}
