package com.example.refreshloadmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.refreshloadmore.adpater.ListItemAdapter
import com.goach.refreshloadmore.RefreshLayout
import kotlinx.android.synthetic.main.activity_recycler_refresh.*

/**
 * 测试RecyclerView只有下拉刷新情况
 */
class RecyclerViewRefreshTestActivity : AppCompatActivity(), RefreshLayout.RefreshListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_refresh)
        val listData = mutableListOf<String>()
        for(i in 0 until 10){
            listData.add("Item $i")
        }
        rcv_list.adapter = ListItemAdapter(listData);
        rlRefresh.setRefreshListener(this).setRefreshing(true)
    }

    override fun onRefresh() {
        Handler().postDelayed({
            rlRefresh.refreshComplete()
        }, 3000)
    }
}
