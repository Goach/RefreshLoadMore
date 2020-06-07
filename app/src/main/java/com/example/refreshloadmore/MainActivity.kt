package com.example.refreshloadmore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnNested.setOnClickListener {
            startActivity(Intent(this,NestedScrollTestActivity::class.java))
        }
        btnRecyclerRefresh.setOnClickListener {
            startActivity(Intent(this,RecyclerViewRefreshTestActivity::class.java))

        }
        btnRecyclerlist.setOnClickListener {
            startActivity(Intent(this,RecyclerViewListTestActivity::class.java))
        }
    }
}
