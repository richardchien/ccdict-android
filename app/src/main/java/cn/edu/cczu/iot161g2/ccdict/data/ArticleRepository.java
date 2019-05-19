package cn.edu.cczu.iot161g2.ccdict.data;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.edu.cczu.iot161g2.ccdict.App;
import cn.edu.cczu.iot161g2.ccdict.beans.Article;
import cn.edu.cczu.iot161g2.ccdict.utils.HttpUtils;

public class ArticleRepository {
    private static final String API_URL_FORMAT_ARTICLE_LIST = "https://dict.youdao.com/infoline/web?client=web&startDate=%s";
    private static final String CACHE_KEY_ARTICLE_LIST = "ARTICLE_LIST";

    public static Article[] getArticlesFromCache() {
        JSONArray array = App.getCache().getJSONArray(CACHE_KEY_ARTICLE_LIST);
        return array != null ? jsonArray2ArticleArray(array) : null;
    }

    public static Article[] getArticlesFromInternet() {
        String dateString = getDateString();
        JSONArray array = null;
        String jsonString = HttpUtils.get(String.format(API_URL_FORMAT_ARTICLE_LIST, dateString));
        if (!"".equals(jsonString)) {
            try {
                JSONObject object = new JSONObject(jsonString);
                array = object.getJSONArray(dateString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (array != null) {
            App.getCache().getDiskCache().put(CACHE_KEY_ARTICLE_LIST, array);
            return jsonArray2ArticleArray(array);
        }
        return null;
    }

    private static String getDateString() {
        Date now = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(now);
    }

    private static Article[] jsonArray2ArticleArray(JSONArray array) {
        Gson gson = new Gson();
        return gson.fromJson(array.toString(), Article[].class);
    }
}
