package cn.edu.cczu.iot161g2.ccdict.events;

import java.util.List;

import cn.edu.cczu.iot161g2.ccdict.beans.DictEntry;

/**
 * 用于通知搜索已经完成.
 */
public class SearchCompletedEvent {
    public final String keyword;
    public final List<DictEntry> results;

    public SearchCompletedEvent(String keyword, List<DictEntry> results) {
        this.keyword = keyword;
        this.results = results;
    }
}
