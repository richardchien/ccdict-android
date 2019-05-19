package cn.edu.cczu.iot161g2.ccdict.events;

public class SearchConfirmedEvent {
    public final String keyword;

    public SearchConfirmedEvent(String keyword) {
        this.keyword = keyword;
    }
}
