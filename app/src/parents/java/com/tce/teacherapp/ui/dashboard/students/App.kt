package com.tce.teacherapp.ui.dashboard.students

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Parcel
import android.os.Parcelable

class App : Parcelable {
    var intent: Intent?
    var resolveInfo: ResolveInfo?

    constructor(intent: Intent?, resolveInfo: ResolveInfo?) {
        this.intent = intent
        this.resolveInfo = resolveInfo
    }

    protected constructor(`in`: Parcel) {
        intent = `in`.readParcelable(Intent::class.java.classLoader)
        resolveInfo = `in`.readParcelable(ResolveInfo::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(intent, flags)
        dest.writeParcelable(resolveInfo, flags)
    }

    companion object {
        val CREATOR: Parcelable.Creator<App?> = object : Parcelable.Creator<App?> {
            override fun createFromParcel(`in`: Parcel): App? {
                return App(`in`)
            }

            override fun newArray(size: Int): Array<App?> {
                return arrayOfNulls(size)
            }
        }
    }
}