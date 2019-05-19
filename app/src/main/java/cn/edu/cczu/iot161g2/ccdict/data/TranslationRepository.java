package cn.edu.cczu.iot161g2.ccdict.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.List;
import java.util.stream.Collectors;

import cn.edu.cczu.iot161g2.ccdict.utils.HttpUtils;
import cn.edu.cczu.iot161g2.ccdict.utils.UrlUtils;

/**
 * 用于获取谷歌翻译结果.
 */
public class TranslationRepository {
    private static final String API_URL_FORMAT = "http://translate.google.cn/translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=auto&tl=%s&q=%s";

    public enum ToLang {
        EN("en-US"), ZH("zh-CN");

        public final String code;

        ToLang(String code) {
            this.code = code;
        }
    }

    public static String getTranslation(String text, ToLang toLang) {
        String res = HttpUtils.get(String.format(API_URL_FORMAT, toLang.code, UrlUtils.urlEncode(text)));
        try {
            Result result = new Gson().fromJson(res, Result.class);
            if (result == null || result.sentences == null || result.sentences.size() < 1) {
                return null;
            }
            return TextUtils.join("", result.sentences.stream().map(sentence -> sentence.trans).collect(Collectors.toList()));
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Result {
        List<Sentence> sentences;

        public Result() {
        }

        public Result(List<Sentence> sentences) {
            this.sentences = sentences;
        }

        private static class Sentence {
            String trans;
            String orig;

            public Sentence() {
            }

            public Sentence(String trans, String orig) {
                this.trans = trans;
                this.orig = orig;
            }
        }
    }
}
