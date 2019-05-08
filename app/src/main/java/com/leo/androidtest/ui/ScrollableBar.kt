package com.leo.androidtest.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.leo.androidtest.R
import com.leo.androidtest.ViewModel.ScrollableBarViewModel
import kotlin.collections.ArrayList

class ScrollableBar: RelativeLayout, View.OnTouchListener, View.OnLayoutChangeListener {


    constructor(ctx: Context) : super(ctx) { }
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {  }
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr:Int) : super(ctx, attrs,defStyleAttr) { }

    private lateinit var mViewModel :ScrollableBarViewModel
    private lateinit var mContext :Context
    private var mBaseTextColor: Int = Color.BLUE
    private var mHighLightTextColor: Int = Color.WHITE
    private var mTargetButton :Button? = null
    private var mIndex :Int = 0
    private var mTotalOffsetX :Float = 0f
    private lateinit var mButtonList :ArrayList<Button>

    var index : Int
        get() = mIndex
        set(value) {
            mIndex = value
            scrollToIndex()
        }

    @SuppressLint("ClickableViewAccessibility")
    fun initialView(ctx: Context,owner: LifecycleOwner,viewModel:ScrollableBarViewModel){
        mContext = ctx
        mViewModel = viewModel
        mButtonList = ArrayList()
        val inflater = LayoutInflater.from(context)
        val zBar = inflater.inflate(R.layout.z_scrollable_bar, this, false)
        val left = zBar.findViewById<Button>(R.id.btn_left)
        val center = zBar.findViewById<Button>(R.id.btn_center)
        val right = zBar.findViewById<Button>(R.id.btn_right)
        val scrollImage = zBar.findViewById<ImageView>(R.id.ScrollImage)
        addView(zBar)

        //add Button to List
        mButtonList.add(0,left)
        mButtonList.add(1,center)
        mButtonList.add(2,right)

        //set OnTouchListener
        left.setOnTouchListener(this)
        center.setOnTouchListener(this)
        right.setOnTouchListener(this)

        //ViewModel
        viewModel.buttonSize.observe(owner, Observer<Int>{
            val params = scrollImage.layoutParams as ConstraintLayout.LayoutParams
            if(it!! <=2 ){
                params.matchConstraintPercentWidth = 0.5f
                right.visibility = View.GONE
            }else{
                params.matchConstraintPercentWidth = 0.3333f
                right.visibility = View.VISIBLE
            }
        })

        viewModel.buttonHorizontal.observe(owner,Observer<Float>{
            val params = scrollImage.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = it!!
            scrollImage.layoutParams = params
        })

        observeScrollableBarButtonViewModel(owner,left,viewModel.leftButtonViewModel)
        observeScrollableBarButtonViewModel(owner,center,viewModel.centerButtonViewModel)
        observeScrollableBarButtonViewModel(owner,right,viewModel.rightButtonViewModel)
    }

   private fun observeScrollableBarButtonViewModel(owner: LifecycleOwner,btn:Button,viewModel:ScrollableBarViewModel.ScrollableBarButtonViewModel){
        viewModel.buttonText.observe(owner,Observer<String>{
            btn.text = it
        })

        viewModel.buttonOnClick.observe(owner,Observer<OnClickListener>{
            btn.setOnClickListener(it)
        })
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == null) {
            return true
        }
        v.parent.requestDisallowInterceptTouchEvent(true)
        if (event!!.pointerCount > 0) {
            if (event.pointerCount > 1 || event.getPointerId(0) > 0) {
                if (event.action != MotionEvent.ACTION_UP && event.action != MotionEvent.ACTION_CANCEL) {
                    return true
                }
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            mTargetButton = v as Button
            mTotalOffsetX = 0f
        }
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            resumeIndex(v)
        }

        return false
    }

    private val gestureDetector = GestureDetector(this.context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {

            val offsetX = (-distanceX / mViewModel.buttonSize.value!!)
            var offsetXPercent = (offsetX / (this@ScrollableBar.measuredWidth - mTargetButton!!.measuredWidth))
            mTotalOffsetX += offsetX
            if (mIndex == 0 && offsetX < 0 && mTotalOffsetX < 0) {
                mTotalOffsetX = 0f
                offsetXPercent = 0f
            } else if (mIndex == mViewModel.buttonSize.value!! - 1 && offsetX > 0 && mTotalOffsetX > 0) {
                mTotalOffsetX = 0f
                offsetXPercent = 0f
            }
            mViewModel.buttonHorizontal.value = mViewModel.buttonHorizontal.value!! + offsetXPercent

            val percent = Math.abs(mTotalOffsetX / this@ScrollableBar.measuredWidth) * 2
            var targetColor = ArgbEvaluator().evaluate(percent, mHighLightTextColor, mBaseTextColor) as Int
            mButtonList[mIndex].setTextColor(targetColor)
            if (mTotalOffsetX > 0 && mIndex < mViewModel.buttonSize.value!! - 1) {
                targetColor = ArgbEvaluator().evaluate(percent, mBaseTextColor, mHighLightTextColor) as Int
                mButtonList[(mIndex + 1)].setTextColor(targetColor)
            } else if (mTotalOffsetX < 0 && mIndex > 0) {
                targetColor = ArgbEvaluator().evaluate(percent, mBaseTextColor, mHighLightTextColor) as Int
                mButtonList[mIndex - 1].setTextColor(targetColor)
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX > 0) {
                    if (mIndex < mViewModel.buttonSize.value!! - 1) {
                        mIndex += 1
                        scrollToIndex()
                    }
                } else if (velocityX < 0) {
                    if (mIndex > 0) {
                        mIndex -= 1
                        scrollToIndex()
                    }
                }
                return true
            }
            return false
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {

            if (mTargetButton != null) {
                val index =  mButtonList.indexOf(mTargetButton!!)
                if ( index >= 0) {
                    mIndex = index
                    scrollToIndex()
                    return true
                }
            }
            return super.onSingleTapUp(e)
        }
    })


    private fun resumeIndex(view: View) {
        if (mTotalOffsetX > view.width / 5) {
            if (mIndex < mViewModel.buttonSize.value!! - 1) {
                mIndex += 1
                scrollToIndex()
            }
        } else if (mTotalOffsetX < -view.width / 5) {
            if (mIndex > 0) {
                mIndex -= 1
                scrollToIndex()
            }
        } else {
            scrollToIndex()
        }
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

    fun scrollToIndex(){
        val move = (1.0f * mIndex / (mViewModel.buttonSize.value!! - 1))
        ValueAnimator.ofFloat(mViewModel.buttonHorizontal.value!!, move).apply {
            duration = 200
            addUpdateListener{
                mViewModel.buttonHorizontal.value = it.animatedValue as Float
            }
            start()
        }

        changeButton(this.findViewById(R.id.btn_left),if (mIndex != 0) this.mBaseTextColor else this.mHighLightTextColor)
        changeButton(this.findViewById(R.id.btn_center),if (mIndex != 1) this.mBaseTextColor else this.mHighLightTextColor)
        changeButton(this.findViewById(R.id.btn_right),if (mIndex != 2) this.mBaseTextColor else this.mHighLightTextColor)
    }

    private fun changeButton(button:Button,color:Int ){
        ValueAnimator.ofObject(ArgbEvaluator(), button.currentTextColor, color).apply {
            duration = 200
            addUpdateListener{
                button.setTextColor(it.animatedValue as Int)
            }
            start()
        }
    }
}
