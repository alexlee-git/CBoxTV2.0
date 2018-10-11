@file:Suppress("UNREACHABLE_CODE")

package com.newtv.cms.bean

import android.os.Parcel
import android.os.Parcelable


data class Program(
    val defaultFocus: Int,
    val contentId: String,
    val contentType: String,
    val img: String,
    val title: String,
    val subTitle: String,
    val l_id: String,
    val l_uuid: String,
    val l_contentType: String,
    val l_actionType: String,
    val l_actionUri: String,
    val l_focusId: String,
    val l_focusParam: String,
    val grade: String,
    val lSuperScript: String,
    val rSuperScript: String,
    val lSubScript: String,
    val rSubScript: String,
    val columnPoint: String,
    val rowPoint: String,
    val columnLength: String,
    val rowHeight: String,
    val cellType: String,
    val cellCode: String,
    val isAd: Int,
    val sortNum: String,
    val seriesSubUUID: String,
    val apk: String,
    val apkPageType: String,
    val apkPageParam: String,
    val specialParam: String,
    val recommendedType: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
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
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(defaultFocus)
        parcel.writeString(contentId)
        parcel.writeString(contentType)
        parcel.writeString(img)
        parcel.writeString(title)
        parcel.writeString(subTitle)
        parcel.writeString(l_id)
        parcel.writeString(l_uuid)
        parcel.writeString(l_contentType)
        parcel.writeString(l_actionType)
        parcel.writeString(l_actionUri)
        parcel.writeString(l_focusId)
        parcel.writeString(l_focusParam)
        parcel.writeString(grade)
        parcel.writeString(lSuperScript)
        parcel.writeString(rSuperScript)
        parcel.writeString(lSubScript)
        parcel.writeString(rSubScript)
        parcel.writeString(columnPoint)
        parcel.writeString(rowPoint)
        parcel.writeString(columnLength)
        parcel.writeString(rowHeight)
        parcel.writeString(cellType)
        parcel.writeString(cellCode)
        parcel.writeInt(isAd)
        parcel.writeString(sortNum)
        parcel.writeString(seriesSubUUID)
        parcel.writeString(apk)
        parcel.writeString(apkPageType)
        parcel.writeString(apkPageParam)
        parcel.writeString(specialParam)
        parcel.writeString(recommendedType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Program> {
        override fun createFromParcel(parcel: Parcel): Program {
            return Program(parcel)
        }

        override fun newArray(size: Int): Array<Program?> {
            return arrayOfNulls(size)
        }
    }
}