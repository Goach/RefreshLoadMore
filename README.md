# RefreshLoadMore
下拉刷新上拉加载的控件

# 引入

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
	        implementation 'com.github.Goach:RefreshLoadMore:1.0.0'
	}
```
# 下拉刷新
```
<?xml version="1.0" encoding="utf-8"?>
<com.goach.refreshloadmore.RefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:id="@+id/rlRefresh"
    android:background="#F5F5F5">

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/white" >
        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:src="@mipmap/scroll_test"/>
    </androidx.core.widget.NestedScrollView>
</com.goach.refreshloadmore.RefreshLayout>
```

```
rlRefresh.setRefreshListener(object:RefreshLayout.RefreshListener{
            override fun onRefresh() {
                Handler().postDelayed({
                    rlRefresh.refreshComplete()
                },3000)
            }
        }).setRefreshing(true)
```

# 下拉刷新上拉加载
```
<?xml version="1.0" encoding="utf-8"?>
<com.goach.refreshloadmore.RefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rlRefresh"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <com.goach.refreshloadmore.LoadMoreRecyclerView
        android:id="@+id/rcv_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#F5F5F5"
        android:orientation="vertical"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>
</com.goach.refreshloadmore.RefreshLayout>
```

```
rlRefresh
            .setRefreshLoadMoreListener(object:RefreshLayout.RefreshLoadMoreListener{
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
                            rlRefresh.refreshComplete(currPage > pageCount)
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

            }).setRefreshing(true)
```

