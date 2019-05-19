package cn.edu.cczu.iot161g2.ccdict.events;

/**
 * 用于通知搜索栏状态变更.
 */
public class SearchStateChangedEvent {
    public final boolean enabled;

    public SearchStateChangedEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
