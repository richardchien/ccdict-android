package cn.edu.cczu.iot161g2.ccdict.beans

import im.r_c.android.dbox.annotation.Column
import im.r_c.android.dbox.annotation.Table

@Table("history")
data class HistoryEntry(@Column(notNull = true) val keyword: String) {
    var id: Long = 0
        private set
}
