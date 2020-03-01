package br.com.flyingdutchman.arena_challenge.ui.common

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.flyingdutchman.arena_challenge.extensions.px
import kotlin.math.roundToInt


class DividerItemDecoration() : RecyclerView.ItemDecoration() {
    val HORIZONTAL = LinearLayout.HORIZONTAL
    val VERTICAL = LinearLayout.VERTICAL

    private val TAG = "DividerItem"
    private val ATTRS = intArrayOf(R.attr.listDivider)

    private var mDivider: Drawable? = null

    private var mOrientation = 0

    private val mBounds = Rect()


    constructor(
        context: Context,
        orientation: Int
    ) : this() {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        if (mDivider == null) {
            Log.w(
                TAG, "@android:attr/listDivider was not set in the theme used for this "
                        + "DividerItemDecoration. Please set that attribute all call setDrawable()"
            )
        }
        a.recycle()
        setOrientation(orientation)
    }


    fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL && orientation != VERTICAL)) { "Invalid orientation. It should be either HORIZONTAL or VERTICAL" }
        mOrientation = orientation
    }

    fun setDrawable(drawable: Drawable) {
        mDivider = drawable
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        c.save()
        val leftWithMargin: Int = 16.px
        val right = parent.width
        val rightWithMargin: Int = right - 16.px

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom: Int =
                mBounds.bottom + ViewCompat.getTranslationY(child).roundToInt()
            val top: Int = bottom - (mDivider?.intrinsicHeight ?: 0)
            mDivider?.setBounds(leftWithMargin, top, rightWithMargin, bottom)
            mDivider?.draw(c)
        }
        c.restore()
    }


}