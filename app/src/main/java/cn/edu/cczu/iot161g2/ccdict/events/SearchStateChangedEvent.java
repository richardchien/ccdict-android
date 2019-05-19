package cn.edu.cczu.iot161g2.ccdict.events;

public class SearchStateChangedEvent {
    public final boolean enabled;

    public SearchStateChangedEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
