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
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.beans.Article;
import cn.edu.cczu.iot161g2.ccdict.data.ArticleRepository;
import im.r_c.android.commonadapter.CommonAdapter;
import im.r_c.android.commonadapter.ViewHolder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BaseAdapter mArticleListViewAdapter;

    private List<Article> mArticleList = new ArrayList<>();

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        mSwipeRefreshLayout = v.findViewById(R.id.srl_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        ListView articleListView = v.findViewById(R.id.lv_article_list);
        mArticleListViewAdapter = new CommonAdapter<Article>(getContext(), mArticleList, R.layout.article_list_item) {
            @Override
            public void onPostBindViewHolder(ViewHolder viewHolder, Article article) {
                viewHolder.setViewText(R.id.tv_item_title, article.getTitle());
                Picasso.get().load(article.getImageUrl()).into((ImageView) viewHolder.getView(R.id.iv_item_image));
            }
        };
        articleListView.setAdapter(mArticleListViewAdapter);
        articleListView.addHeaderView(getLayoutInflater().inflate(R.layout.article_list_header, articleListView, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadArticleListFromCache();
        loadArticleListFromInternet();
    }

    private void onRefresh() {
        loadArticleListFromInternet();
    }

    private void loadArticleListFromCache() {
        Observable<Article[]> observable = Observable.fromCallable(ArticleRepository::getArticlesFromCache)
                .filter(Objects::nonNull); // 如果没有缓存则直接忽略
        subscribeArticles(observable);
    }

    private void loadArticleListFromInternet() {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
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
