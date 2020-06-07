package com.example.refreshloadmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.goach.refreshloadmore.RefreshLayout
import kotlinx.android.synthetic.main.activity_nested_scoll.*

/**
 * 测试NestedScrollView下拉刷新
 */
class NestedScrollTestActivity : AppCompatActivity() ,RefreshLayout.RefreshListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nested_scoll)
        rlRefresh.setRefreshListener(this).setRefreshing(true)
    }

    override fun onRefresh() {
        Handler().postDelayed({
            rlRefresh.refreshComplete()
        },3000)
    }
}
