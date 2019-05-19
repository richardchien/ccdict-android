package cn.edu.cczu.iot161g2.ccdict.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 提供 URL 处理相关方法.
 */
public class UrlUtils {
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
