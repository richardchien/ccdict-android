package cn.edu.cczu.iot161g2.ccdict.beans;

import androidx.annotation.NonNull;

import java.util.Objects;

import im.r_c.android.dbox.annotation.Column;
import im.r_c.android.dbox.annotation.Table;

@Table("history")
public class HistoryEntry {
    private long id;

    @Column(notNull = true)
    private String keyword;

    public HistoryEntry() {
    }

    public HistoryEntry(String keyword) {
        this.keyword = keyword;
    }

    public long getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryEntry entry = (HistoryEntry) o;
        return id == entry.id &&
                Objects.equals(keyword, entry.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keyword);
    }

    @NonNull
    @Override
    public String toString() {
        return "HistoryEntry{" +
                "id=" + id +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
