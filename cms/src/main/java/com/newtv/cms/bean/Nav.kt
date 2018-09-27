package com.newtv.cms.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:05
 * 创建人:           weihaichao
 * 创建日期:          2018/9/21
 */

data class Nav(
        val pageType: String,
        val currentIcon: String,
        val isFocus: String,
        val defaultIcon: String,
        val widthIcon: String,
        val logo: String,
        val id: String,
        val title: String,
        val isMenu: String,
        val focusIcon: String,
        val poster: String,
        val child: ArrayList<Nav>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(CREATOR)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pageType)
        parcel.writeString(currentIcon)
        parcel.writeString(isFocus)
        parcel.writeString(defaultIcon)
        parcel.writeString(widthIcon)
        parcel.writeString(logo)
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(isMenu)
        parcel.writeString(focusIcon)
        parcel.writeString(poster)
        parcel.writeTypedList(child)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Nav> {
        override fun createFromParcel(parcel: Parcel): Nav {
            return Nav(parcel)
        }

        override fun newArray(size: Int): Array<Nav?> {
            return arrayOfNulls(size)
        }
    }
}