package com.goach.refreshloadmore

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.loadmore_footer.view.*

/**
 * Goach All Rights Reserved
 *User: Goach
 *Email: goach0728@gmail.com
 *Des:加载更多
 */
class LoadMoreFooter : RelativeLayout {
    private var mStateEndText: String? = ""

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context)
    }

    private fun initView(ctx: Context) {
        val moreView = LayoutInflater.from(ctx)
            .inflate(R.layout.loadmore_footer, this, false) as RelativeLayout
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 0, 0, 0)
        this.layoutParams = lp
        setPadding(0, 0, 0, 0)
        addView(
            moreView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        )
        gravity = Gravity.BOTTOM
    }

    /**
     * 没有更多数据的文案
     * @param stateEndText
     */
    fun setStateEndText(stateEndText: String?) {
        mStateEndText = stateEndText
    }

    fun setState(state: Int) {
        tv_footer_hint.visibility = View.INVISIBLE
        pb_load_more.visibility = View.INVISIBLE
        if (state == STATE_LOADING) {
            pb_load_more.visibility = View.VISIBLE
            tv_footer_hint.setText(R.string.refresh_header_hint_loading)
        } else if (state == STATE_END) {
            tv_footer_hint.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(mStateEndText)) {
                tv_footer_hint.text = mStateEndText
            } else {
                tv_footer_hint.setText(R.string.refresh_footer_hint_nomore)
            }
        }
    }

    /**
     * normal status
     */
    fun normal() {
        tv_footer_hint.visibility = View.VISIBLE
        pb_load_more.visibility = View.GONE
    }

    /**
     * loading status
     */
    fun loading() {
        tv_footer_hint.visibility = View.GONE
        pb_load_more.visibility = View.VISIBLE
    }

    /**
     * hide footer when disable pull load more
     */
    fun hide() {
        val lp =
            rl_load_more.layoutParams as LayoutParams
        lp.height = 0
        rl_load_more.layoutParams = lp
    }

    /**
     * show footer
     */
    fun show() {
        val lp = rl_load_more.layoutParams as LayoutParams
        lp.height = LayoutParams.WRAP_CONTENT
        rl_load_more.layoutParams = lp
    }

    fun setLoadMoreBgColor(color: Int) {
        rl_load_more.setBackgroundColor(color)
    }

    companion object {
        const val STATE_LOADING = 1//加载中
        const val STATE_END = 2 //没有数据了
    }
}