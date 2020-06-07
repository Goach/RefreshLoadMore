package com.goach.refreshloadmore

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.refresh_header.view.*
import java.util.*

/**
 * Goach All Rights Reserved
 *User: Goach
 *Email: goach0728@gmail.com
 *Des:下拉刷新头部
 */
class RefreshHeader : LinearLayout {
    private val rotateAnimDuration: Long = 180

    private var mContainer: LinearLayout? = null
    private var mState = STATE_NORMAL
    private lateinit var mRotateUpAnim: Animation
    private lateinit var mRotateDownAnim: Animation
    private var mMeasuredHeight = 0

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context)
    }

    constructor(ctx: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        ctx,
        attributeSet,
        defStyleAttr
    ) {
        initView(ctx)
    }

    private fun initView(ctx: Context) {
        mContainer =
            LayoutInflater.from(ctx).inflate(R.layout.refresh_header, this, false) as LinearLayout
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 0, 0, 0)
        this.layoutParams = lp
        setPadding(0, 0, 0, 0)
        addView(mContainer, LayoutParams(LayoutParams.MATCH_PARENT, 0))// 初始情况，设置下拉刷新view高度为0
        gravity = Gravity.BOTTOM
        mRotateUpAnim = RotateAnimation(
            0.0f, -180.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mRotateUpAnim.duration = rotateAnimDuration
        mRotateUpAnim.fillAfter = true
        mRotateDownAnim = RotateAnimation(
            -180.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mRotateDownAnim.duration = rotateAnimDuration
        mRotateDownAnim.fillAfter = true
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mMeasuredHeight = measuredHeight
    }

    fun setRefreshHeaderBgColor(color: Int) {
        if (mContainer != null) mContainer!!.setBackgroundColor(color)
    }

    fun setArrowImageView(resId: Int) {
        iv_header_arrow.setImageResource(resId)
    }// 显示箭头图片

    // 显示进度
    var state: Int
        get() = mState
        set(state) {
            if (state == mState) return
            when (state) {
                STATE_REFRESHING -> {    // 显示进度
                    iv_header_arrow.clearAnimation()
                    iv_header_arrow.visibility = View.INVISIBLE
                    pb_header.visibility = View.VISIBLE
                }
                STATE_DONE -> {
                    iv_header_arrow.visibility = View.INVISIBLE
                    pb_header.visibility = View.INVISIBLE
                }
                else -> {    // 显示箭头图片
                    iv_header_arrow.visibility = View.VISIBLE
                    pb_header.visibility = View.INVISIBLE
                }
            }
            when (state) {
                STATE_NORMAL -> {
                    if (mState == STATE_RELEASE_TO_REFRESH) {
                        iv_header_arrow.startAnimation(mRotateDownAnim)
                    }
                    if (mState == STATE_REFRESHING) {
                        iv_header_arrow.clearAnimation()
                    }
                    tv_header_hint.setText(R.string.refresh_header_hint_normal)
                }
                STATE_RELEASE_TO_REFRESH -> if (mState != STATE_RELEASE_TO_REFRESH) {
                    iv_header_arrow.clearAnimation()
                    iv_header_arrow.startAnimation(mRotateUpAnim)
                    tv_header_hint.setText(R.string.refresh_header_hint_ready)
                }
                STATE_REFRESHING -> tv_header_hint.setText(R.string.refresh_header_hint_loading)
                STATE_DONE -> tv_header_hint.setText(R.string.refresh_header_hint_done)
                else -> {
                }
            }
            mState = state
        }

    fun refreshComplete() {
        tv_header_time.text = friendlyTime(Date())
        state = STATE_DONE
        Handler().postDelayed({ reset() }, 200)
    }

    var visibleHeight: Int
        get() {
            val lp =
                mContainer!!.layoutParams as LayoutParams
            return lp.height
        }
        set(height) {
            val lp = mContainer!!.layoutParams as LayoutParams
            lp.height = if (height < 0) 0 else height
            mContainer!!.layoutParams = lp
        }

    fun onMove(delta: Float) {
        if (visibleHeight > 0 || delta > 0) {
            visibleHeight += delta.toInt()
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                state = if (visibleHeight > mMeasuredHeight) {
                    STATE_RELEASE_TO_REFRESH
                } else {
                    STATE_NORMAL
                }
            }
        }
    }

    fun releaseAction(): Boolean {
        var isOnRefresh = false
        val height = visibleHeight
        if (height == 0) // not visible.
            isOnRefresh = false
        if (visibleHeight > mMeasuredHeight && mState < STATE_REFRESHING) {
            state = STATE_REFRESHING
            isOnRefresh = true
        }
        var destHeight = 0 // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight
        }
        smoothScrollTo(destHeight)
        return isOnRefresh
    }

    private fun reset() {
        smoothScrollTo(0)
        Handler().postDelayed({ state = STATE_NORMAL }, 500)
    }

    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(visibleHeight, destHeight)
        animator.setDuration(300).start()
        animator.addUpdateListener { animation -> visibleHeight = animation.animatedValue as Int }
        animator.start()
    }

    private fun friendlyTime(time: Date): String? {//获取time距离当前的秒数
        val ct = ((System.currentTimeMillis() - time.time) / 1000).toInt()
        if (ct == 0) {
            return context.resources.getString(R.string.refresh_header_time_0)
        }
        if (ct in 1..59) {
            return "${ct}${context.resources.getString(R.string.refresh_header_time_1)}"
        }
        if (ct in 60..3599) {
            return "${(ct / 60).coerceAtLeast(1)}${context.resources
                .getString(R.string.refresh_header_time_2)}"
        }
        if (ct in 3600..86399) return "${ct / 3600}${context.resources.getString(R.string.refresh_header_time_3)}"

        if (ct in 86400..2591999) { //86400 * 30
            val day = ct / 86400
            return "${day}${context.resources.getString(R.string.refresh_header_time_4)}"
        }
        return if (ct in 2592000..31103999) { //86400 * 30
            "${ct / 2592000}${context.resources
                .getString(R.string.refresh_header_time_5)}"
        } else "${ct / 31104000}${context.resources.getString(R.string.refresh_header_time_6)}"
    }

    companion object {
        const val STATE_NORMAL = 0
        const val STATE_RELEASE_TO_REFRESH = 1
        const val STATE_REFRESHING = 2
        const val STATE_DONE = 3
    }
}