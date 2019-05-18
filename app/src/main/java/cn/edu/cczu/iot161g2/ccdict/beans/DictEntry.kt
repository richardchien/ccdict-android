package cn.edu.cczu.iot161g2.ccdict.beans

import im.r_c.android.dbox.annotation.Column
import im.r_c.android.dbox.annotation.Table

@Table("dictionary")
data class DictEntry(
        @Column(notNull = true) val word: String,
        @Column(notNull = true) val explanation: String) {
    var id: Long = 0
        private set
}
