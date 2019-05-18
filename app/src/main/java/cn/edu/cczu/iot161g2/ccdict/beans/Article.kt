package cn.edu.cczu.iot161g2.ccdict.beans

import com.google.gson.annotations.SerializedName

data class Article(val title: String, val url: String,
                   @SerializedName("image-mobile2") val imageUrl: String)
