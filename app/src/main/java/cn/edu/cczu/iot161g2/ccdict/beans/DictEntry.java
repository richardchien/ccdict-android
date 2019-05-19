package cn.edu.cczu.iot161g2.ccdict.beans;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import im.r_c.android.dbox.annotation.Column;
import im.r_c.android.dbox.annotation.Table;

@Table("dictionary")
public class DictEntry implements Serializable {
    private long id;

    @Column(notNull = true)
    private String word;

    @Column(notNull = true)
    private String explanation;

    public DictEntry() {
    }

    public DictEntry(String word, String explanation) {
        this.word = word;
        this.explanation = explanation;
    }

    public long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<String> getExplanations() {
        return Arrays.asList(explanation.split("\n"));
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictEntry dictEntry = (DictEntry) o;
        return id == dictEntry.id &&
                Objects.equals(word, dictEntry.word) &&
                Objects.equals(explanation, dictEntry.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, word, explanation);
    }

    @NonNull
    @Override
    public String toString() {
        return "DictEntry{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
