package cn.edu.cczu.iot161g2.ccdict;

import android.app.Application;

import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;
import cn.edu.cczu.iot161g2.ccdict.utils.DictHelper;
import im.r_c.android.dbox.DBox;
import im.r_c.android.fusioncache.FusionCache;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * 应用程序类.
 * 用于进行全局的初始化, 包括建立缓存, 初始化数据库, 导入默认词典.
 */
public class App extends Application {
    private static FusionCache sFusionCache;

    public static FusionCache getCache() {
        return sFusionCache;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        sFusionCache = new FusionCache(this, 4 * 1024 * 1024, 50 * 1024 * 1024);
        DBox.init(this, "main.db");

        if (!DictHelper.hasDict()) {
            Observable.just("dict.json")
                    .doOnNext(filename -> DBox.of(DictEntry.class).drop())
                    .map(filename -> DictHelper.importFromAssets(this, filename))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }
}
