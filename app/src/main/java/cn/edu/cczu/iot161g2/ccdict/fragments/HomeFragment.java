package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.activities.ArticleActivity;
import cn.edu.cczu.iot161g2.ccdict.beans.Article;
import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;
import cn.edu.cczu.iot161g2.ccdict.data.ArticleRepository;
import cn.edu.cczu.iot161g2.ccdict.databinding.ArticleListHeaderBinding;
import cn.edu.cczu.iot161g2.ccdict.events.SearchCompletedEvent;
import cn.edu.cczu.iot161g2.ccdict.utils.DictHelper;
import im.r_c.android.commonadapter.CommonAdapter;
import im.r_c.android.commonadapter.ViewHolder;
import im.r_c.android.dbox.DBox;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mArticleListView;
    private BaseAdapter mArticleListViewAdapter;
    private View mArticleListHeaderView;

    private List<Article> mArticleList = new ArrayList<>();
    private DictEntry mDailyWord;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initView(v);
        return v;
    }

    private void initView(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.srl_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        mArticleListView = view.findViewById(R.id.lv_article_list);
        mArticleListViewAdapter = new CommonAdapter<Article>(getContext(), mArticleList, R.layout.article_list_item) {
            @Override
            public void onPostBindViewHolder(ViewHolder viewHolder, Article article) {
                viewHolder.setViewText(R.id.tv_item_title, article.getTitle());
                Picasso.get().load(article.getImageUrl()).into((ImageView) viewHolder.getView(R.id.iv_item_image));
            }
        };
        mArticleListView.setAdapter(mArticleListViewAdapter);

        mArticleListView.setOnItemClickListener((parent, v, position, id) -> {
            if (v == mArticleListHeaderView) {
                EventBus.getDefault().post(new SearchCompletedEvent(mDailyWord.getWord(), Collections.singletonList(mDailyWord)));
            } else {
                Article article = (Article) mArticleListViewAdapter.getItem(position);
                ArticleActivity.start(getContext(), article.getUrl());
            }
        });

        initDailyWord();
    }

    private void initDailyWord() {
        if (!DictHelper.hasDict()) {
            return;
        }

        List<DictEntry> entries = DBox.of(DictEntry.class).findAll().results().all(); // this is bad
        Random random = new Random(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        mDailyWord = entries.get(random.nextInt(entries.size()));

        ArticleListHeaderBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.article_list_header, mArticleListView, false);
        binding.setEntry(mDailyWord);
        mArticleListHeaderView = binding.getRoot();
        mArticleListView.addHeaderView(mArticleListHeaderView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadArticleListFromCache();
        loadArticleListFromInternet();
    }

    private void onRefresh() {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        loadArticleListFromInternet();
    }

    private void loadArticleListFromCache() {
        Observable<Article[]> observable = Observable.fromCallable(ArticleRepository::getArticlesFromCache)
                .filter(Objects::nonNull); // 如果没有缓存则直接忽略
        subscribeArticles(observable);
    }

    private void loadArticleListFromInternet() {
        Observable<Article[]> observable = Observable.fromCallable(ArticleRepository::getArticlesFromInternet);
        subscribeArticles(observable);
    }

    private void subscribeArticles(Observable<Article[]> observable) {
        observable
                .flatMap(Observable::fromArray)
                .filter(article -> !TextUtils.isEmpty(article.getUrl()))
                .collectInto(new ArrayList<Article>(), List::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    mArticleList.clear();
                    mArticleList.addAll(articles);
                    mArticleListViewAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }, throwable -> Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show());
    }
}
