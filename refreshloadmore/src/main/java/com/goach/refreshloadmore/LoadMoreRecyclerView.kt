package com.goach.refreshloadmore

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Goach All Rights Reserved
 *User: Goach
 *Email: goach0728@gmail.com
 *Des:加载更多RecyclerView
 */
class LoadMoreRecyclerView : RecyclerView {
    private val typeFooterAdapter = 10001
    private var loadMoreEnable: Boolean = true
    private var mLoadFooter: LoadMoreFooter? = null
    private var mIsLoadingData = false//是否在刷新
    private var mIsNoMore = false//是否已经没有数据了
    private var mWrapAdapter: WrapAdapter? = null
    private val mDataObserver: AdapterDataObserver = DataObserver()


    private var mLoadMoreListener: LoadMoreListener? = null

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
        if (loadMoreEnable) {
            mLoadFooter = LoadMoreFooter(ctx)
            mLoadFooter!!.visibility = View.VISIBLE
            hideFooter()
        }
    }

    override fun setAdapter(adapter: Adapter<ViewHolder>?) {
        super.setAdapter(adapter)
        if (adapter == null) {
            return
        }
        mWrapAdapter = WrapAdapter(adapter)
        super.setAdapter(mWrapAdapter)
        adapter.registerAdapterDataObserver(mDataObserver)
        mDataObserver.onChanged()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE && mLoadMoreListener != null && !mIsLoadingData && loadMoreEnable) {
            val layoutManager = layoutManager ?: return
            val lastVisibleItemPosition: Int
            lastVisibleItemPosition = when (layoutManager) {
                is GridLayoutManager -> {
                    layoutManager.findLastVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    val into = IntArray(layoutManager.spanCount)
                    layoutManager.findLastVisibleItemPositions(into)
                    findMax(into)
                }
                else -> {
                    (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
                }
            }
            var isNoRefreshing = false
            if (parent is RefreshLayout) {
                if ((parent as RefreshLayout).getRefreshHeader() != null) {
                    isNoRefreshing =
                        (parent as RefreshLayout).getRefreshHeader()!!.state < RefreshHeader.STATE_REFRESHING
                }
            }
            if (layoutManager.childCount > 0 && lastVisibleItemPosition >= layoutManager.itemCount - 1 && layoutManager.itemCount >= layoutManager.childCount && !mIsNoMore
                && isNoRefreshing
            ) {
                mIsLoadingData = true
                mLoadFooter?.setState(LoadMoreFooter.STATE_LOADING)
                mLoadMoreListener?.onLoadMore()
            }
        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    fun loadMoreComplete(noMore: Boolean) {
        mIsLoadingData = false
        mIsNoMore = noMore
        mLoadFooter?.setState(if (mIsNoMore) LoadMoreFooter.STATE_END else LoadMoreFooter.STATE_LOADING)
        showFooter()
    }

    fun showFooter() {
        mLoadFooter?.show()
    }

    fun hideFooter() {
        mLoadFooter?.hide()
    }

    fun setLoadingMoreEnabled(enabled: Boolean) {
        loadMoreEnable = enabled
        if (!enabled) {
            mLoadFooter?.setState(LoadMoreFooter.STATE_END)
        }
    }

    fun setLoadMoreListener(listener: LoadMoreListener) {
        mLoadMoreListener = listener
    }

    interface LoadMoreListener {
        fun onLoadMore()
    }

    inner class DataObserver : AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter?.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemMoved(fromPosition, toPosition)
        }
    }

    inner class WrapAdapter(private val outSideAdapter: Adapter<ViewHolder>) :
        Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == typeFooterAdapter && mLoadFooter != null) {
                return SimpleViewHolder(mLoadFooter!!)
            }
            return outSideAdapter.onCreateViewHolder(parent, viewType)
        }

        override fun getItemCount(): Int {
            return if (loadMoreEnable) {
                outSideAdapter.itemCount + 1
            } else {
                outSideAdapter.itemCount
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position < outSideAdapter.itemCount) {
                outSideAdapter.onBindViewHolder(holder, position)
            }
        }

        override fun getItemViewType(position: Int): Int {
            check(!isReservedItemViewType(outSideAdapter.getItemViewType(position))) { "LoadMoreRecyclerView require itemViewType in adapter should be not be $typeFooterAdapter" }
            if (isFooter(position)) {
                return typeFooterAdapter
            }
            val adapterCount = outSideAdapter.itemCount
            return if (position < adapterCount) {
                outSideAdapter.getItemViewType(position)
            } else super.getItemViewType(position)
        }

        override fun getItemId(position: Int): Long {
            return if (position < outSideAdapter.itemCount) {
                outSideAdapter.getItemId(position)
            } else super.getItemId(position)
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            val manager = recyclerView.layoutManager
            if (manager is GridLayoutManager) {
                manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isFooter(position)) manager.spanCount else 1
                    }
                }
            }
            outSideAdapter.onAttachedToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            outSideAdapter.onDetachedFromRecyclerView(recyclerView)
        }

        override fun onViewAttachedToWindow(holder: ViewHolder) {
            super.onViewAttachedToWindow(holder)
            val lp = holder.itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
                && (isFooter(holder.layoutPosition))
            ) {
                lp.isFullSpan = true
            }
            outSideAdapter.onViewAttachedToWindow(holder)
        }

        override fun onViewDetachedFromWindow(holder: ViewHolder) {
            super.onViewDetachedFromWindow(holder)
            outSideAdapter.onViewAttachedToWindow(holder)
        }

        override fun onViewRecycled(holder: ViewHolder) {
            super.onViewRecycled(holder)
            outSideAdapter.onViewRecycled(holder)
        }

        override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
            return outSideAdapter.onFailedToRecycleView(holder)
        }

        override fun unregisterAdapterDataObserver(observer: AdapterDataObserver) {
            super.unregisterAdapterDataObserver(observer)
            outSideAdapter.unregisterAdapterDataObserver(observer)
        }

        override fun registerAdapterDataObserver(observer: AdapterDataObserver) {
            super.registerAdapterDataObserver(observer)
            outSideAdapter.registerAdapterDataObserver(observer)
        }

        private fun isFooter(position: Int): Boolean {
            return if (loadMoreEnable) {
                position == itemCount - 1
            } else {
                false
            }
        }

        private fun isReservedItemViewType(itemViewType: Int): Boolean {
            return itemViewType == typeFooterAdapter
        }

        private inner class SimpleViewHolder(itemView: View) : ViewHolder(itemView)
    }
}