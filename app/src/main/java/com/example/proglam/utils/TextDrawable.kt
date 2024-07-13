package com.example.proglam.utils

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint


class TextDrawable(text: String, color: Int, size: Float) : Drawable() {
    private val paint = TextPaint().also {
        it.color = color
        it.textSize = size
        it.isAntiAlias = true
        it.isFakeBoldText = true
        it.setShadowLayer(6f, 0f, 0f, Color.BLACK)
        it.style = Paint.Style.FILL
        it.textAlign = Paint.Align.LEFT
    }

    private val textWidth = Rect().let {
        paint.getTextBounds(
            text, 0, when (text.contains("\n")) {
                true -> text.indexOf("\n")
                false -> text.length
            }, it
        )

        it.right
    }

    private var staticLayout =
        StaticLayout.Builder
            .obtain(text, 0, text.length, paint, textWidth)
            .build()

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        staticLayout.draw(canvas)
        canvas.restore()
    }

    override fun getIntrinsicWidth(): Int {
        return staticLayout.width
    }

    override fun getIntrinsicHeight(): Int {
        return staticLayout.height
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.setColorFilter(cf)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}