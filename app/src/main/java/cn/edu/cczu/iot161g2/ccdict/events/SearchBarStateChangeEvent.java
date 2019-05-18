package cn.edu.cczu.iot161g2.ccdict.events;

public class SearchBarStateChangeEvent {
    public final boolean enabled;

    public SearchBarStateChangeEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
