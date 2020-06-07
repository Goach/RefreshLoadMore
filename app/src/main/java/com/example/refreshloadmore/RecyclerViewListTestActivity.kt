package com.example.refreshloadmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.refreshloadmore.adpater.ListItemAdapter
import com.goach.refreshloadmore.RefreshLayout
import kotlinx.android.synthetic.main.activity_recycler_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 测试RecyclerView只有下拉刷新加载更多情况
 */
class RecyclerViewListTestActivity : AppCompatActivity(), RefreshLayout.RefreshLoadMoreListener {
    private val listData = mutableListOf<String>()
    private val pageCount = 4
    private var currPage = 1
    private lateinit var listItemAdapter: ListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_list)
        listItemAdapter = ListItemAdapter(listData)
        rcv_list.adapter = listItemAdapter
        rlRefresh
            .setRefreshLoadMoreListener(this).setRefreshing(true)
    }

    override fun onRefresh() {
        currPage = 1
        GlobalScope.launch {
            delay(10000)
            listData.clear()
            for (i in 0 until 10) {
                listData.add("Item $i")
            }
            launch(Dispatchers.Main) {
                listItemAdapter.notifyDataSetChanged()
                rlRefresh.refreshComplete()
            }
        }
    }

    override fun onLoadMore() {
        GlobalScope.launch {
            delay(1000)
            for (i in 0 until 10) {
                listData.add("Item $i")
            }
            launch(Dispatchers.Main) {
                listItemAdapter.notifyDataSetChanged()
                currPage++
                rlRefresh.loadMoreComplete(currPage > pageCount)
            }
        }
    }
}
