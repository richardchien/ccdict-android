package cn.edu.cczu.iot161g2.ccdict;

import android.app.Application;

import im.r_c.android.fusioncache.FusionCache;

public class App extends Application {
    private static FusionCache sFusionCache;

    public static FusionCache getCache() {
        return sFusionCache;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sFusionCache = new FusionCache(this, 4 * 1024 * 1024, 50 * 1024 * 1024);
    }
}
