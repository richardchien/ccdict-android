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

/**
 * 首页.
 */
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

        initDailyWord(); // 初始化每日单词组件
    }

    private void initDailyWord() {
        if (!DictHelper.hasDict()) {
            return;
        }

        // 从词典中以日期为种子随机选择一个单词, 作为每日单词
        List<DictEntry> entries = DBox.of(DictEntry.class).findAll().results().all(); // this is bad
        Random random = new Random(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        mDailyWord = entries.get(random.nextInt(entries.size()));

        // 使用了 DataBinding 机制, 降低页面和控制层的耦合
        ArticleListHeaderBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.article_list_header, mArticleListView, false);
        binding.setEntry(mDailyWord);
        mArticleListHeaderView = binding.getRoot();
        mArticleListView.addHeaderView(mArticleListHeaderView); // 将每日单词组件添加到文章列表的头部
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadArticleListFromCache(); // 先从缓存获取文章列表, 以避免长时间不显示内容
        loadArticleListFromInternet(); // 再从网络获取最新文章
    }

    private void onRefresh() {
        // 下拉刷新
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
                    // 刷新文章成功, 更新列表
                    mArticleList.clear();
                    mArticleList.addAll(articles);
                    mArticleListViewAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }, throwable -> Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show());
    }
}
