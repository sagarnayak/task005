package com.example.task005.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.example.task005.contracts.ResourcesMaster

class ResourcesMaster(private val context: Context) : ResourcesMaster() {

    override fun getString(stringId: Int): String {
        return context.getString(stringId)
    }

    override fun getString(stringName: String): String {
        return getString(context.resources.getIdentifier(stringName, "string", context.packageName))
    }

    override fun getDrawable(drawableId: Int): Drawable? {
        return try {
            ResourcesCompat.getDrawable(context.resources, drawableId, null)
        } catch (ex: Exception) {
            null
        }
    }

    override fun getDrawable(drawableStringName: String): Drawable? {
        return try {
            ResourcesCompat.getDrawable(
                context.resources,
                context.resources.getIdentifier(
                    drawableStringName,
                    "drawable",
                    context.packageName
                ),
                null
            )
        } catch (ex: Exception) {
            null
        }
    }

    override fun getBitmapFromId(drawableId: Int?, drawableName: String?): Bitmap? {
        var drawable: Drawable? = null
        drawableId?.let {
            drawable = getDrawable(it)
        } ?: run {
            drawableName?.let { name ->
                drawable = getDrawable(name)
            }
        }
        drawable?.let {
            val bitmap = Bitmap.createBitmap(
                it.intrinsicWidth,
                it.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            return bitmap
        } ?: run {
            return null
        }
    }

    override fun getColor(colorId: Int): Int {
        return ResourcesCompat.getColor(
            context.resources,
            colorId,
            null
        )
    }

    override fun getColorStateList(colorId: Int): ColorStateList {
        return AppCompatResources.getColorStateList(
            context, colorId
        )
    }
}