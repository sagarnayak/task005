package com.example.task005.contracts

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

abstract class ResourcesMaster() {

    abstract fun getString(stringId: Int): String

    abstract fun getString(stringName: String): String

    abstract fun getDrawable(drawableId: Int): Drawable?

    abstract fun getDrawable(drawableStringName: String): Drawable?

    abstract fun getBitmapFromId(drawableId: Int? = null, drawableName: String? = null): Bitmap?

    abstract fun getColor(colorId: Int): Int

    abstract fun getColorStateList(colorId: Int): ColorStateList
}