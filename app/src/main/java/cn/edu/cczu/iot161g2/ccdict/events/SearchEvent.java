package cn.edu.cczu.iot161g2.ccdict.events;

/**
 * 用于触发搜索操作.
 */
public class SearchEvent {
    public final String keyword;

    public SearchEvent(String keyword) {
        this.keyword = keyword;
    }
}
