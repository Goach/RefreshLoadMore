package com.goach.refreshloadmore

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * Goach All Rights Reserved
 *User: Goach
 *Email: goach0728@gmail.com
 *Des:下拉刷新View
 */
class RefreshLayout : LinearLayout {
    private val dragRate = 3f
    private var pullRefreshEnabled: Boolean = true
    private var mRefreshHeader: RefreshHeader? = null
    private var mScrollView: View? = null
    private var mLastY: Float = -1f
    private var mIsLoadingData = false//是否在刷新
    private var mRefreshListener: RefreshListener? = null

    constructor(ctx: Context) : super(ctx) {
        init(ctx)
    }

    constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet) {
        init(ctx)
    }

    constructor(ctx: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        ctx,
        attributeSet,
        defStyleAttr
    ) {
        init(ctx)
    }

    private fun init(ctx: Context) {
        orientation = VERTICAL
        if (pullRefreshEnabled) {
            mRefreshHeader = RefreshHeader(ctx)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        var index = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView is RecyclerView) {
                mScrollView = childView
                index = i
            } else if (childView is NestedScrollView) {
                mScrollView = childView
                index = i
            }
        }
        mRefreshHeader?.let {
            addView(it, index)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = ev.rawY
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY: Float = ev.rawY - mLastY
                if (diffY > 0 && isTop()) {
                    return true
                }
            }
            else -> {
                mLastY = -1f
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = event.rawY
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY: Float = event.rawY - mLastY
                mLastY = event.rawY
                if (isTop() && pullRefreshEnabled) {
                    mRefreshHeader?.let {
                        it.onMove(diffY / dragRate)
                        if (it.visibleHeight > 0 && it.state < RefreshHeader.STATE_REFRESHING) {
                            return false
                        }
                    }
                }
            }
            else -> {
                mLastY = -1f
                if (isTop() && pullRefreshEnabled) {
                    mRefreshHeader?.let {
                        if (it.releaseAction()) {
                            if (!mIsLoadingData) {
                                mIsLoadingData = true
                                mRefreshListener?.onRefresh()
                            } else {
                                refreshComplete()
                            }
                        }
                    }
                }
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun isTop(): Boolean {
        if (mScrollView is RecyclerView) {
            return !(mScrollView as RecyclerView).canScrollVertically(-1)
        }
        if (mScrollView is NestedScrollView) {
            return (mScrollView as NestedScrollView).scrollY <= 0
        }
        return false
    }

    fun setPullRefreshEnabled(enabled: Boolean) {
        this.pullRefreshEnabled = enabled
    }

    fun setRefreshing(refreshing: Boolean) {
        if (!mIsLoadingData && refreshing && pullRefreshEnabled) {
            mRefreshHeader?.let {
                it.state = RefreshHeader.STATE_REFRESHING
                it.post {
                    if (it.measuredHeight.toFloat() == 0f) {
                        it.onMove(
                            TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                60f, resources.displayMetrics
                            )
                        )
                    } else {
                        it.onMove(it.measuredHeight.toFloat())
                    }
                }
            }
            mIsLoadingData = true
            mRefreshListener?.onRefresh()
            if (mScrollView is LoadMoreRecyclerView) {
                (mScrollView as LoadMoreRecyclerView).hideFooter()
            }
        }
    }

    fun setRefreshListener(listener: RefreshListener): RefreshLayout {
        this.mRefreshListener = listener
        return this
    }

    fun setRefreshLoadMoreListener(listener: RefreshLoadMoreListener): RefreshLayout {
        setRefreshListener(listener)
        if (mScrollView is LoadMoreRecyclerView) {
            (mScrollView as LoadMoreRecyclerView).setLoadMoreListener(object :
                LoadMoreRecyclerView.LoadMoreListener {
                override fun onLoadMore() {
                    listener.onLoadMore()
                }
            })
        }
        return this
    }

    fun refreshComplete() {
        mIsLoadingData = false
        mRefreshHeader?.refreshComplete()
        if (mScrollView is LoadMoreRecyclerView) {
            (mScrollView as LoadMoreRecyclerView).showFooter()
        }
    }

    fun loadMoreComplete(noMore: Boolean) {
        if (mScrollView is LoadMoreRecyclerView) {
            (mScrollView as LoadMoreRecyclerView).loadMoreComplete(noMore)
        }
    }

    fun getRefreshHeader(): RefreshHeader? {
        return mRefreshHeader
    }

    interface RefreshLoadMoreListener : RefreshListener {
        fun onLoadMore()
    }

    interface RefreshListener {
        fun onRefresh()
    }
}