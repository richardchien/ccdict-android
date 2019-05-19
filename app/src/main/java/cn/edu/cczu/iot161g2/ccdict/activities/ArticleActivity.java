package cn.edu.cczu.iot161g2.ccdict.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.edu.cczu.iot161g2.ccdict.R;

public class ArticleActivity extends AppCompatActivity {
    private static final String TAG = "ArticleActivity";

    private static final String PARAM_URL = "url";

    public static void start(Context context, String url) {
        Intent starter = new Intent(context, ArticleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_URL, url);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        WebView webView = findViewById(R.id.wv_article);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = getIntent().getStringExtra(PARAM_URL);
        Log.d(TAG, "url: " + url);
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}
