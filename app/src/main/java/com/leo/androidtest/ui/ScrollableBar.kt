package com.leo.androidtest.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.leo.androidtest.R

class ScrollableBar: RelativeLayout, View.OnTouchListener, View.OnLayoutChangeListener {

    constructor(ctx: Context) : super(ctx) { initialView(ctx) }
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) { initialView(ctx) }
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr:Int) : super(ctx, attrs,defStyleAttr) { initialView(ctx) }

    private lateinit var mContext :Context
    private fun initialView(ctx: Context){
        mContext = ctx
        val inflater = LayoutInflater.from(context)
        val zBar = inflater.inflate(R.layout.z_scrollable_bar, this, false)

        addView(zBar)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {

    }

}
